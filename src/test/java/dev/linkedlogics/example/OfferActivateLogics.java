package dev.linkedlogics.example;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.error;
import static dev.linkedlogics.LinkedLogicsBuilder.exit;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;
import dev.linkedlogics.service.local.LocalServiceConfigurer;
import lombok.Data;

public class OfferActivateLogics {
	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;
	public static final String GET_SUBSCRIBER_INFO = "GET_SUBSCRIBER_INFO";
	public static final String SEND_FAIL_NOTIFICATION = "SEND_FAIL_NOTIFICATION";
	public static final String SEND_SUCCESS_NOTIFICATION = "SEND_SUCCESS_NOTIFICATION";
	public static final String ACTIVATE_OFFER = "ACTIVATE_OFFER";
	public static final String DEACTIVATE_OFFER = "DEACTIVATE_OFFER";
	public static final String NGBSS_ADAPTER = "ngbss_adapter";
	
	public static final String STATUS = "ACTIVE";
	public static final String TYPE = "PREPAID";
	public static final Integer HOUR = 19; //LocalDateTime.now().getHour();
	
	public static ProcessDefinition offerActivate() {
		return createProcess(ACTIVATE_OFFER, 0)
				.add(getSubscriberInfo())
				.add(branch(checkNotActive(), sendFailNotification("NOT_ACTIVE")).build())
				.add(branch(checkNotPrepaid(), sendFailNotification("NOT_PREPAID")).build())
				.add(branch(checkNotTime(), sendFailNotification("NOT_TIME")).build())
				.add(activateOffer("OFFER_1")
						.compensate(deactivateOffer("OFFER_1"))
						.handle(error().errorLogic(sendFailNotification("NO_BALANCE")).build()).build())
				.add(sendSuccessNotification("SUCCESS"))
						.add(verify(expr("false")).disabled().build())
				.add(activateOffer("OFFER_2").delayed(5).build())
				.build();
	}
	
	private static SingleLogicDefinition getSubscriberInfo() {
		return logic(GET_SUBSCRIBER_INFO).application(NGBSS_ADAPTER).build();
	}
	
	private static BaseLogicDefinition sendFailNotification(String template) {
		return group(logic(SEND_FAIL_NOTIFICATION).input("msisdn", expr("msisdn")).input("template", template).build(),
				exit().build()).build();
	}
	
	private static SingleLogicDefinition sendSuccessNotification(String template) {
		return logic(SEND_SUCCESS_NOTIFICATION).input("msisdn", expr("msisdn")).input("template", template).build();
	}
	
	private static SingleLogicBuilder activateOffer(String offerId) {
		return logic(ACTIVATE_OFFER).input("msisdn", expr("msisdn")).input("offer", offerId);
	}
	
	private static SingleLogicDefinition deactivateOffer(String offerId) {
		return logic(DEACTIVATE_OFFER).input("msisdn", expr("msisdn")).input("offer", offerId).build();
	}
	
	private static ExpressionLogicDefinition checkNotActive() {
		return expr("subscriber.status != 'ACTIVE'");
	}
	
	private static ExpressionLogicDefinition checkNotPrepaid() {
		return expr("subscriber.type != 'PREPAID'");
	}
	
	private static ExpressionLogicDefinition checkNotTime() {
		return expr("hour < 18 || hour > 22");
	}
	
	@Logic(id = GET_SUBSCRIBER_INFO, returnAs = "subscriber")
	public static Subscriber getSubscriberInfo(@Input("msisdn") String msisdn) {
		Subscriber subscriber = new Subscriber();
		subscriber.setMsisdn(msisdn);
		subscriber.setStatus(STATUS);
		subscriber.setType(TYPE);
		return subscriber;
	}
	
	@Logic(id = SEND_FAIL_NOTIFICATION)
	public static void sendFailNotification(@Input("msisdn") String msisdn, @Input("template") String template) {
		System.out.println(String.format("%s notifying failure %s with template %s", getDate(), msisdn, template));
	}
	
	@Logic(id = SEND_SUCCESS_NOTIFICATION)
	public static void sendSuccessNotification(@Input("msisdn") String msisdn, @Input("template") String template) {
		System.out.println(String.format("%s notifying success %s with template %s", getDate(), msisdn, template));
	}
	
	@Logic(id = ACTIVATE_OFFER)
	public static void activateOffer(@Input("msisdn") String msisdn, @Input("offer") String offer) {
		System.out.println(String.format("%s activating offer %s for %s", getDate(), offer, msisdn));
	}
	
	@Logic(id = DEACTIVATE_OFFER)
	public static void deactivateOffer(@Input("msisdn") String msisdn, @Input("offer") String offer) {
		System.out.println(String.format("%s deactivating offer %s for %s", getDate(), offer, msisdn));
	}

	private static String getDate() {
		return formatter.format(LocalDateTime.now()).substring(0, 8);
	}
	
	@Data
	public static class Subscriber {
		private String status;
		private String type;
		private String msisdn;
	}
	
	public static void main(String[] args) {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(OfferActivateLogics.class);
		LinkedLogics.registerProcess(OfferActivateLogics.class);
		
		LinkedLogics.start(ACTIVATE_OFFER, Map.of("msisdn", "994702011365", "hour", HOUR));
		
		try {
			Thread.sleep(10000);
			System.exit(0);
		} catch (InterruptedException e) {}
	}
}
