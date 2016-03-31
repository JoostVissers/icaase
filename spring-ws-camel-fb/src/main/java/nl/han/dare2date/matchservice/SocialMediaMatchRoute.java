package nl.han.dare2date.matchservice;

//import nl.han.dare2date.matchservice.model.MatchResponse;

import com.sun.org.apache.xpath.internal.operations.Bool;
import nl.ead.webservice.dao.IPersistenceConnector;
import nl.ead.webservice.dao.MySQLConnector;
import nl.ead.webservice.services.IPaymentAPI;
import nl.ead.webservice.services.PayPalEndpoint;
import nl.han.dare2date.matchservice.model.CalculateResponse;
import nl.han.dare2date.matchservice.model.FundingInstruments;
import nl.han.dare2date.matchservice.model.SubscriptionMethod;
import nl.han.dare2date.matchservice.model.SubscriptionResult;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.twitter.TwitterConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import java.sql.Date;
import java.time.LocalDate;

public class SocialMediaMatchRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        JaxbDataFormat jaxbMatchResponse = new JaxbDataFormat(CalculateResponse.class.getPackage().getName());
        Namespaces ns = new Namespaces("mes", "http://www.han.nl/schemas/messages");

        from("spring-ws:rootqname:{http://www.han.nl/schemas/messages}CalculateRequest?endpointMapping=#matchEndpointMapping")
                .log("Na From")
                .setExchangePattern(ExchangePattern.InOut)
                .convertBodyTo(String.class) // we'll handle with the request as XML using XPath
                .choice()
                .when(body().contains("PayPal"))
                .log("PAYPAL")
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

                        IPaymentAPI paymentAPI = new PayPalEndpoint(paramList);
                        if (paymentAPI.doPayment(14.99d)) {
                            System.out.println("GEDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAn!");
                            SubscriptionResult result = new SubscriptionResult();
                            result.setMessage("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
                            result.setAmountPayed(14.99d);
                            result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());

                            CalculateResponse cr = new CalculateResponse();
                            cr.setResult(result);
                            exchange.getIn().setBody(cr);
                        } else {
                            System.out.println("NIET GEDAAN");
                        }
////                                    persistence.savePaymentLog(userID, Date.valueOf(LocalDate.now()).toString() + "Amount: " + subscriptionAmount);
////                                    persistence.saveUserPaypalInfo(new UserPayPalInfo(
////                                            userID,
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getCreditcardNumber().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getCreditcardType().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getExpireMonth().get(0).toString(),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getExpireYear().get(0).toString(),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getCvv2().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getFirstName().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getLastName().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getLine1().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getCity().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getState().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getPostalCode().get(0),
////                                            req.getInput().getPaypalParamlist().getFundingInstruments().getCountryCode().get(0))
////                                    );
//                                    exchange.getIn().setBody("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
////                                    result.setAmountPayed(subscriptionAmount);
////                                    result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
//                            }
                    }
                })
                .end()
                .marshal(jaxbMatchResponse); // serialize the java-object from the aggregrator to SOAP/XML;;
    }
}
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
