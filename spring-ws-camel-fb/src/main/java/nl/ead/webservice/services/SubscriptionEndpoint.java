package nl.ead.webservice.services;

//import nl.han.dare2date.matchservice.model.MatchResponse;

import com.sun.org.apache.xpath.internal.operations.Bool;
import nl.ead.webservice.dao.IPersistenceConnector;
import nl.ead.webservice.dao.MySQLConnector;
import nl.ead.webservice.model.UserPayPalInfo;
import nl.ead.webservice.services.IPaymentAPI;
import nl.ead.webservice.services.PayPalEndpoint;
import nl.han.dare2date.matchservice.model.CalculateResponse;
import nl.han.dare2date.matchservice.model.FundingInstruments;
import nl.han.dare2date.matchservice.model.SubscriptionMethod;
import nl.han.dare2date.matchservice.model.SubscriptionResult;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.twitter.TwitterConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import java.sql.Date;
import java.time.LocalDate;

public class SubscriptionEndpoint extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        JaxbDataFormat jaxbMatchResponse = new JaxbDataFormat(CalculateResponse.class.getPackage().getName());
        Namespaces ns = new Namespaces("mes", "http://www.han.nl/schemas/messages");

        final IPersistenceConnector persistenceConnector = new MySQLConnector();


        from("spring-ws:rootqname:{http://www.han.nl/schemas/messages}CalculateRequest?endpointMapping=#matchEndpointMapping")
                .setExchangePattern(ExchangePattern.InOut)
                .convertBodyTo(String.class) // we'll handle with the request as XML using XPath
                .setHeader("userName", ns.xpath("/mes:CalculateRequest/mes:userName/text()", String.class))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader("UserID", persistenceConnector.getUserIDByName(exchange.getIn().getHeader("userName").toString()));
                    }
                })

                .choice()
                .when(header("userID").isGreaterThan(0))
                .choice()
                .when(body().contains("PayPal"))
                .log("PAYPAL")
                .log("body: ${body}")
                .setHeader("creditcardNumber", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:creditcardNumber/text()", String.class))
                .setHeader("creditcardType", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:creditcardType/text()", String.class))
                .setHeader("expireMonth", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:expire_month/text()", Integer.class))
                .setHeader("expireYear", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:expire_year/text()", Integer.class))
                .setHeader("cvv2", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:cvv2/text()", String.class))
                .setHeader("firstName", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:first_name/text()", String.class))
                .setHeader("lastName", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:last_name/text()", String.class))
                .setHeader("line1", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:line1/text()", String.class))
                .setHeader("city", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:city/text()", String.class))
                .setHeader("state", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:state/text()", String.class))
                .setHeader("postalCode", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:postal_code/text()", String.class))
                .setHeader("countryCode", ns.xpath("/mes:CalculateRequest/mes:paypalParamlist/mes:funding_instruments/mes:country_code/text()", String.class))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        FundingInstruments paramList = new FundingInstruments();
                        paramList.getCreditcardType().add(exchange.getIn().getHeader("creditcardType").toString());
                        paramList.getCreditcardNumber().add(exchange.getIn().getHeader("creditcardNumber").toString());
                        paramList.getExpireMonth().add((Integer) exchange.getIn().getHeader("expireMonth"));
                        paramList.getExpireYear().add((Integer) exchange.getIn().getHeader("expireYear"));
                        paramList.getCvv2().add(exchange.getIn().getHeader("cvv2").toString());
                        paramList.getFirstName().add(exchange.getIn().getHeader("firstName").toString());
                        paramList.getLastName().add(exchange.getIn().getHeader("lastName").toString());
                        paramList.getLine1().add(exchange.getIn().getHeader("line1").toString());
                        paramList.getCity().add(exchange.getIn().getHeader("city").toString());
                        paramList.getState().add(exchange.getIn().getHeader("state").toString());
                        paramList.getPostalCode().add(exchange.getIn().getHeader("postalCode").toString());
                        paramList.getCountryCode().add(exchange.getIn().getHeader("countryCode").toString());

                        System.out.println("before paramlist");
                        System.out.println(exchange.getIn().getHeaders());
                        System.out.println("after paramlist");
                        IPaymentAPI paymentAPI = new PayPalEndpoint(paramList);
                        if (paymentAPI.doPayment(14.99d)) {
                            System.out.println("GELUKT!!!DFGSHTG");
                            Long userID = Long.parseLong(exchange.getIn().getHeader("userID").toString());

                            persistenceConnector.savePaymentLog(userID, Date.valueOf(LocalDate.now()).toString() + "Amount: " + 14.99d);
                            persistenceConnector.saveUserPaypalInfo(new UserPayPalInfo(
                                    userID,
                                    paramList.getCreditcardNumber().get(0),
                                    paramList.getCreditcardType().get(0),
                                    paramList.getExpireMonth().get(0).toString(),
                                    paramList.getExpireYear().get(0).toString(),
                                    paramList.getCvv2().get(0),
                                    paramList.getFirstName().get(0),
                                    paramList.getLastName().get(0),
                                    paramList.getLine1().get(0),
                                    paramList.getCity().get(0),
                                    paramList.getState().get(0),
                                    paramList.getPostalCode().get(0),
                                    paramList.getCountryCode().get(0))

                            );

                            SubscriptionResult result = new SubscriptionResult();
                            result.setMessage("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
                            result.setAmountPayed(14.99d);
                            result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());

                            CalculateResponse cr = new CalculateResponse();
                            cr.setResult(result);
                            exchange.getIn().setBody(cr);

                        } else {
                            SubscriptionResult result = new SubscriptionResult();
                            result.setMessage("payment unsuccessfull: " + Date.valueOf(LocalDate.now()).toString());
                            result.setAmountPayed(0);
                            result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());

                            CalculateResponse cr = new CalculateResponse();
                            cr.setResult(result);
                            exchange.getIn().setBody(cr);
                            System.out.println("NIET GEDAAN");
                        }
                    }
                })
                .endChoice()
                .otherwise()
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        SubscriptionResult result = new SubscriptionResult();
                        result.setMessage("Payment unsuccessfull - User does not exist: " + Date.valueOf(LocalDate.now()).toString());
                        result.setAmountPayed(0d);
                        result.setPaymentMethod("None");

                        CalculateResponse cr = new CalculateResponse();
                        cr.setResult(result);
                        exchange.getIn().setBody(cr);
                    }
                })
                .end()
                .marshal(jaxbMatchResponse); // serialize the java-object from the aggregrator to SOAP/XML;;

    }
}


//persistence.savePaymentLog(userID, Date.valueOf(LocalDate.now()).toString() + "Amount: " + subscriptionAmount);
////                                    persistence.saveUserPaypalInfo(new UserPayPalInfo(
////                                            userID,
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getCreditcardNumber().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getCreditcardType().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getExpireMonth().get(0).toString(),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getExpireYear().get(0).toString(),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getCvv2().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getFirstName().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getLastName().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getLine1().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getCity().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getState().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getPostalCode().get(0),
////                                            req.getInput().getPaypalparamList().getFundingInstruments().getCountryCode().get(0))
////                                    );
//                                    exchange.getIn().setBody("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
////                                    result.setAmountPayed(subscriptionAmount);
////                                    result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
//                            }
//
//    .setHeader("test",ns.xpath("/mes:CalculateRequest/mes:userName/text()", String.class))
//    .process(new Processor() {
//        public void process(Exchange exchange) throws Exception {
//            boolean userExists = userExits(exchange.getIn().getHeader("test").toString());
//        }
//    })
//    public boolean userExits(String username){
////        IPersistenceConnector persistence = new MySQLConnector();
////        Long userID = persistence.getUserIDByName(username);
////        System.out.println(userID.toString());
//        return true;
//    }

//.split(ns.xpath("//mes:CalculateRequest/*"), new SocialMediaMatchAggregrate()) // split the request into four separate parts


// .marshal(jaxbMatchResponse); // serialize the java-object from the aggregrator to SOAP/XML;

//                .log("BODY!!!!:${body}");
//        .end();


//                .choice()
//                .when(userExits())
//                .end()
//            .choice()
//                .when(body().contains("PayPal")) // send the twitterName to twitter and wait for the aggregrate to do something useful
//                    .log("PayPal: ${body}")
//                    .setHeader(TwitterConstants.TWITTER_KEYWORDS, ns.xpath("/mes:twitterName/text()", String.class)) // fill the keyword parameter
//                .to("twitter://search")
//                .otherwise() // send the facebookid to FB and wait for the aggregrate to do something useful
//                .setHeader("CamelFacebook.userId", ns.xpath("/mes:facebookid/text()", String.class)) // fill the userid parameter
//                .to("facebook://user")
//            .end() // end the parallel processing, this is a kind of "join"
//        .end() // stop splitting and start returning
//


//package nl.ead.webservice.services;
//
//import nl.ead.webservice.*;
//import nl.ead.webservice.dao.IPersistenceConnector;
//import nl.ead.webservice.model.User;
//import nl.ead.webservice.model.UserPayPalInfo;
//import nl.han.dare2date.matchservice.model.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//
//import java.sql.Date;
//import java.time.LocalDate;
//
//@Endpoint
//public class SubscriptionEndpoint {
//    private final IPersistenceConnector persistence;
//    private final double subscriptionAmount;
//    private IPaymentAPI paymentAPI;
//
//    @Autowired
//    public SubscriptionEndpoint(IPersistenceConnector persistence) {
//        this.persistence = persistence;
//        subscriptionAmount = 14.99;
//    }
//
//    @PayloadRoot(localPart = "CalculateRequest", namespace = "http://www.han.nl/schemas/messages")
//    @ResponsePayload
//    public CalculateResponse calculateSumForName(@RequestPayload CalculateRequest req) {
//
//        Long userID = persistence.getUserIDByName(req.getInput().getUserName());
//        System.out.println(userID.toString());
//        SubscriptionMethod method = req.getInput().getSubscriptionMethod();
//        SubscriptionResult result = new SubscriptionResult();
//
//        if(userID == 0 ){
//            CalculateResponse resp = new CalculateResponse();
//            result.setMessage("User does not exists");
//            resp.setResult(result);
//            return resp;
//        }
//        else{
//            if(method.equals(SubscriptionMethod.PAY_PAL)){
//                FundingInstruments paramList = req.getInput().getPaypalparamList().getFundingInstruments();
//                paymentAPI = new PayPalEndpoint(paramList);
//                if(paymentAPI.doPayment(subscriptionAmount)){
//                    persistence.savePaymentLog(userID, Date.valueOf(LocalDate.now()).toString() + "Amount: " + subscriptionAmount );
//                    persistence.saveUserPaypalInfo(new UserPayPalInfo(
//                            userID,
//                            req.getInput().getPaypalparamList().getFundingInstruments().getCreditcardNumber().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getCreditcardType().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getExpireMonth().get(0).toString(),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getExpireYear().get(0).toString(),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getCvv2().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getFirstName().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getLastName().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getLine1().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getCity().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getState().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getPostalCode().get(0),
//                            req.getInput().getPaypalparamList().getFundingInstruments().getCountryCode().get(0))
//                    );
//                    result.setMessage("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
//                    result.setAmountPayed(subscriptionAmount);
//                    result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
//                }
//                else{
//                    result.setMessage("Paypal payment failed: " + Date.valueOf(LocalDate.now()).toString());
//                    result.setAmountPayed(0);
//                    result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
//                }
//            }
//            else if(method.equals(SubscriptionMethod.BITCOIN)){
//                //FundingInstruments paramList = req.getInput().getBitcoinparamList();
//                result.setMessage("Bitcoin payment failed");
//            }
//
//            CalculateResponse resp = new CalculateResponse();
//            resp.setResult(result);
//
//            return resp;
//        }
//    }
//}
