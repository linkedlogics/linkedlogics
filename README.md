# Linked-Logics Framework #

Linked-Logics is a decentralized workflow execution engine for building distributed, resilient and scalable applications. Linked-Logics has a different approach and is a good candidate for introducing [Sagas](https://microservices.io/patterns/data/saga.html) in microservices. It combines both `orchestration` and `choreography` together by providing DSL for **orchestration** and decentralized execution like in **choreography**. It is very simple to use and has minimal framework footprint.

**Main features:**
- It is decentralized (no single point of failure)
- It is fully event-driven, no syncronous calls
- It is backed by Kafka, RabbitMQ etc.
- It has minimal framework footprint (just one single annotation `@Logic`)
- It provides very powerfull DSL for building complex workflows
- It supports expression language to customize workflows using Groovy, SpEL, JS etc.
- It supports versioning of workflows and logics
- It provides powerfull **compensation** mechanism and error handling required in **Sagas**
- It provides standard **timeout** and **retry** mechanisms
- It supports easy **fork** and **join** workflows
- It supports **asynchronous** logics

## High-Level Architecture ##

![high level design](design/images/hld.png)

### Distributed Cache ###
Distributed cache stores active workflow contexts and workflow definitions.

### Workflow Microservices ###
Microservices are individual applications which take part in workflow execution. They listen to their own queue in the messaging bus and consume only messages related to them. While designing workflows, we need to indicate which microservice will be responsible for each workflow item. Whole architecture is deployed as decentralized model where each microservice takes responsibility to execute the current workflow item and decide on next steps by storing the latest context in cache, identifying next execution step, sending metrics etc.

### Messaging Bus ###
Messaging bus is required since whole architecture is a synchronous. Messaging bus provides different queues for each microservice. 

## Low-Level Architecture ##

![low level design](design/images/lld.png)

Framework add several low level components(services) to each microservice. These services performs basic operations required for workflow execution such as consuming and publishing workflow step info to messaging bus, retrieving and storing latest context information in cache, actual logic execution and error handling. It also follows workflow execution and decides on next steps. Conditional and scripting workflow items are executed on current microservices. Only workflow items requiring a microservice to execute are published to messaging bus.

## Example ##

### Logic ###
Logic is an executable part of workflow which is executed inside its owner microservice. Logics are defined by `id` which is unique within its owner. Any public method can be defined as a logic by using `@Logic` annotation.
#### Charging Microservice ####
```
package io.linkedlogics.sample.charging;

public class ChargingLogics {
	private ChargingService chargingService;
	private RefundService refundService;
	
	@Logic(id = CHARGE_CUSTOMER, returnAs = "charging_result")
	public boolean chargeCustomer(@Input("customer") Customer customer, @Input("amount") Double amount) {
		double chargedAmount = chargingService.charge(customer.getCustomerId(), amount);
		return chargedAmount > 0;
	}
	
	@Logic(id = REFUND_CUSTOMER, returnAs = "refund_result")
	public boolean refundCustomer(@Input("customer") Customer customer, @Input("amount") Double amount) {
		double refundedAmount = refundService.refund(customer.getCustomerId(), amount);
		return refundedAmount > 0;
	}
}
```
#### Order Microservice ####
```
package io.linkedlogics.sample.order;

public class OrderLogics {
	private OrderService orderService;
	
	@Logic(id = CREATE_ORDER, returnAs = "order")
	public Order createOrder(@Input("customer") Customer customer, @Input("itemId") String itemId) {
		Order order = orderService.create(customer.getCustomerId(), itemId);
		if (!order.isSuccess()) {
			throw new LogicException(-1, "Order creation failed", ErrorType.PERMANENT);
		}
		return order;
	}
}
```
### Process ###
Process is workflow definition. Any class can provide process objects, it just needs to have methods returning `ProcessDefinition`. In below example we are calling two logics from two different microservices with compensation logics. Workflow will trigger compenstaion in case any failure occurs before execution is finished.
#### Process Definition ####
```
package io.linkedlogics.sample.process;

public class Processes {
	
	public static ProcessDefinition createNewOrderProcess() {
		return createProcess("NEW_ORDER", 0)
				.add(logic(CHARGE_CUSTOMER).application(CHARGING_SERVICE)
						.inputs("customer", var("customer"), "amount", 1.25)
						.compensate(logic(REFUND_CUSTOMER).application(CHARGING_SERVICE)
						 	.input("customer", var("customer"))
						 	.input("amount", 1.25)))
				.add(branch(when("charging_result == true"), 
						logic(CREATE_ORDER).application(ORDER_SERVICE)
							.inputs("customer", var("customer"), "itemId", "ITEM_1")))
				.build();
	}
}
```
### Execution ###
Process can be initiated from any microservice as following: 
#### Process Execution ####
```
package io.linkedlogics.sample.process;

public class Main {
	
	public static void main(String[] args)  {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		
		LinkedLogics.start(newContext("NEW_ORDER").params(new HashMap<>() {{ put("customer", customer);}}).build());
	}
}
```

### Testing ###
Processes also can be tested using mocks.
#### Process Testing ####
```
package io.linkedlogics.sample.process;

@ExtendWith(LinkedLogicsExtension.class)
@LinkedLogicsRegister(Processes.class)
public class Tests {
	
	@Test
	public void testSuccess() {
		mockLogic("CHARGE_CUSTOMER").returnAs("charging_result").thenReturn(true);
		mockLogic("CREATE_ORDER").returnAs("order").thenReturn(new Order());
	
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		
		LinkedLogics.start(newContext("NEW_ORDER").params("customer", customer).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").asBranch().isSatisfied();
		assertContext().when("2").asBranch().leftBranch().isExecuted();
	}
	
	@Test
	public void testFailure() {
		mockLogic("CHARGE_CUSTOMER").returnAs("charging_result").thenReturn(true);
		mockLogic("CREATE_ORDER").thenThrowError(-1, "create order failed");
		mockLogic("REFUND_CUSTOMER").returnAs("refund_result").thenReturn(true);
	
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		
		LinkedLogics.start(newContext("NEW_ORDER").params("customer", customer).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isCompensated();
		assertContext().when("2").asBranch().isSatisfied();
		assertContext().when("2").asBranch().leftBranch().isNotExecuted();
	}
}
```