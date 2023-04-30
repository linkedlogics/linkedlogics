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

## Example ##

### Logic ###
Logic is an executable part of workflow which is executed inside its owner microservice. Logics are defined by `id` which is unique within its owner. Any public method can be defined as a logic by using `@Logic` annotation.
#### Charging Microservice ####
```
package dev.linkedlogics.sample.charging;

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
package dev.linkedlogics.sample.order;

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
package dev.linkedlogics.sample.process;

public class Processes {
	
	public static ProcessDefinition createNewOrderProcess() {
		return createProcess("NEW_ORDER", 0)
				.add(logic(CHARGE_CUSTOMER)
						.application(CHARGING_SERVICE)
						.input("customer", expr("customer"))
						.input("amount", 1.25)
						.compensate(logic(REFUND_CUSTOMER)
							.application(CHARGING_SERVICE)
						 	.input("customer", expr("customer"))
						 	.input("amount", 1.25)
						 .build())
					.build())
				.add(branch(expr("charging_result == true"), 
						logic(CREATE_ORDER)
							.application(ORDER_SERVICE)
							.input("customer", expr("customer"))
							.input("itemId", "ITEM_1")
						.build())
					.build())
				.build();
	}
}
```
### Execution ###
Process can be initiated from any microservice as following: 
#### Process Execution ####
```
package dev.linkedlogics.sample.process;

public class Main {
	
	public static void main(String[] args)  {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		
		LinkedLogics.start("NEW_ORDER", new HashMap<>() {{ put("customer", customer);}});
	}
}
```
