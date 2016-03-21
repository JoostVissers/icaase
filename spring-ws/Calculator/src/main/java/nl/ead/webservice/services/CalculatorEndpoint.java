package nl.ead.webservice.services;

import nl.ead.webservice.*;
import nl.ead.webservice.dao.IPersistenceConnector;
import nl.ead.webservice.model.User;
import nl.ead.webservice.model.UserPayPalInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.sql.Date;
import java.time.LocalDate;

@Endpoint
public class CalculatorEndpoint {
    private final IPersistenceConnector persistence;
    private final double subscriptionAmount;
    private IPaymentAPI paymentAPI;

    @Autowired
    public CalculatorEndpoint(IPersistenceConnector persistence) {
        this.persistence = persistence;
        subscriptionAmount = 14.99;
    }

    @PayloadRoot(localPart = "CalculateRequest", namespace = "http://www.han.nl/schemas/messages")
    @ResponsePayload
    public CalculateResponse calculateSumForName(@RequestPayload CalculateRequest req) {

        Long userID = persistence.getUserIDByName(req.getInput().getUserName());
        System.out.println(userID.toString());
        SubscriptionMethod method = req.getInput().getSubscriptionMethod();
        SubscriptionResult result = new SubscriptionResult();

        if(userID == 0 ){
            CalculateResponse resp = new CalculateResponse();
            result.setMessage("User does not exists");
            resp.setResult(result);
            return resp;
        }

        if(method.equals(SubscriptionMethod.PAY_PAL)){
            FundingInstruments paramList = req.getInput().getPaypalParamlist().getFundingInstruments();
            paymentAPI = new PayPalEndpoint(paramList);
            if(paymentAPI.doPayment(subscriptionAmount)){
                persistence.savePaymentLog(userID, Date.valueOf(LocalDate.now()).toString() + "Amount: " + subscriptionAmount );
                persistence.saveUserPaypalInfo(new UserPayPalInfo(
                        userID,
                        req.getInput().getPaypalParamlist().getFundingInstruments().getCreditcardNumber().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getCreditcardType().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getExpireMonth().get(0).toString(),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getExpireYear().get(0).toString(),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getCvv2().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getFirstName().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getLastName().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getLine1().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getCity().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getState().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getPostalCode().get(0),
                        req.getInput().getPaypalParamlist().getFundingInstruments().getCountryCode().get(0))
                );
                result.setMessage("Paypal payment successfull: " + Date.valueOf(LocalDate.now()).toString());
                result.setAmountPayed(subscriptionAmount);
                result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
            }
            else{
                result.setMessage("Paypal payment failed: " + Date.valueOf(LocalDate.now()).toString());
                result.setAmountPayed(0);
                result.setPaymentMethod(SubscriptionMethod.PAY_PAL.toString());
            }
        }
        else if(method.equals(SubscriptionMethod.BITCOIN)){
            //FundingInstruments paramList = req.getInput().getBitcoinParamlist();
            result.setMessage("Paypal payment failed");
        }

        CalculateResponse resp = new CalculateResponse();
        resp.setResult(result);

        return resp;
    }


}
