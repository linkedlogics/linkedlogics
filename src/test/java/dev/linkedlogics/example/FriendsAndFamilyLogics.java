package dev.linkedlogics.example;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;

import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;

public class FriendsAndFamilyLogics {
	public static final String FF_STOP = "ff_stop";
	
	public static final String SEND_NOTIFICATION = "send_notification";
	public static final String GET_FF_INFO = "get_ff_info";
	public static final String REMOVE_OFFER = "remove_offer";
	public static final String REMOVE_OFFERS = "remove_offers";
	
	public static final String NGBSS_ADAPTER = "ngbss_adapter";
	
	public static ProcessDefinition ffStop() {
		return createProcess(FF_STOP, 0)
				.add(getRequesterAorB())
				.add(branch(isRequesterAorB(), handleA(), handleB()).build())
				.build();
	}
	
	private static BaseLogicDefinition handleA() {
		return branch(isStopAll(), removeAll(), removeB()).build();
	}
	
	private static BaseLogicDefinition handleB() {
		return branch(isStopAll(), sendNotification("WRONG_SMS"), removeB()).build();
	}
	
	private static GroupLogicDefinition removeB() {
		return group(removeOffer(expr("request.msisdn_b")).build(),
					 branch(isLastB(), removeA(), sendNotification("TEMPLATE_B")).build())
			  .build();
	}
	
	private static GroupLogicDefinition removeA() {
		return group(removeOffer(expr("request.msisdn_a")).build(),
				sendNotification("TEMPLATE_A")).build();
	}
	
	private static GroupLogicDefinition removeAll() {
		return group(removeOffers(), removeA()).build();
	}
	
	private static SingleLogicDefinition getRequesterAorB() {
		return logic(GET_FF_INFO).input("msisdn", expr("request.msisdn")).returnAs("ff").application(NGBSS_ADAPTER).build();
	}
	
	private static ExpressionLogicDefinition isRequesterAorB() {
		return expr("msisdn == ff.msisdn_a");
	}
	
	private static ExpressionLogicDefinition isLastB() {
		return expr("ff.numbers.length == 1");
	}
	
	private static SingleLogicBuilder removeOffer(ExpressionLogicDefinition removeMsisdn) {
		return logic(REMOVE_OFFER).input("msisdn", removeMsisdn).input("offer_id", "FF_OFFER_ID").application(NGBSS_ADAPTER);
	}
	
	private static SingleLogicDefinition removeOffers() {
		return logic(REMOVE_OFFERS).input("msisdn", expr("request.msisdn_a")).input("offer_id", "FF_OFFER_ID").application(NGBSS_ADAPTER).build();
	}
	
	private static SingleLogicDefinition sendNotification(String template) {
		return logic(SEND_NOTIFICATION).input("template", template).build();
	}
	
	private static ExpressionLogicDefinition isStopAll() {
		return expr("request.text == 'STOPALL'");
	}
}
