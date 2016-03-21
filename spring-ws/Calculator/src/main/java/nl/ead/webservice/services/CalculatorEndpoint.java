package nl.ead.webservice.services;

import java.util.List;

import com.paypal.api.payments.FundingInstrument;
import nl.ead.webservice.*;
import nl.ead.webservice.dao.ICalculationDao;
import nl.ead.webservice.dao.IPersistenceConnector;
import nl.ead.webservice.model.Calculation;
import nl.ead.webservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CalculatorEndpoint {
    private final ICalculationDao calculationDao;
    private final IPersistenceConnector persistence;
    private IPaymentAPI paymentAPI;

    @Autowired
    public CalculatorEndpoint(ICalculationDao calculationDao,IPersistenceConnector persistence) {
        this.calculationDao = calculationDao;
        this.persistence = persistence;

    }

    @PayloadRoot(localPart = "CalculateRequest", namespace = "http://www.han.nl/schemas/messages")
    @ResponsePayload
    public CalculateResponse calculateSumForName(@RequestPayload CalculateRequest req) {
        // Veranderen naar pay
        // a sequence of a minimum of 1 and unbounded max is generated as a
        // List<>

        SubscriptionMethod method = req.getInput().getSubscriptionMethod();
        SubscriptionResult result = new SubscriptionResult();
        // Doe hier betaal dingen
        // Haal subscription request gegevens uit het request.

        // Controleer welke betaal methode gekozen is
            // If bitcoin
                // doe betaal dingetje voor paypal
            // If paypal
                // doe betaal dingetje voor paypal
            // Als betaling geverifeerd zijn
                // Sla payment log op in database
                // Ecrypt betaal gegevens en sla deze op
                // Return bericht yay succes!
                // Return bericht whops shit is is nie goe nie.

            //Anders return bericht geen geldige methode opgegeven

        if(method.equals(SubscriptionMethod.PAY_PAL)){
            FundingInstruments paramList = req.getInput().getPaypalParamlist().getFundingInstruments();
            paymentAPI = new PayPalEndpoint(paramList);
            if(paymentAPI.doPayment(12.02)){
                //sla shit op
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP");
                System.out.println(persistence.toString());
                System.out.println(req.getInput().getUserName());
                persistence.savePaymentLog(new User(req.getInput().getUserName()),"gedaan nu enzo");
                result.setMessage("Paypal");
            }
            else{
                result.setMessage("Paypal FAIL");
            }
        }
        else if(method.equals(SubscriptionMethod.BITCOIN)){
            //FundingInstruments paramList = req.getInput().getBitcoinParamlist();
            result.setMessage("Bitcoin FAIL");
        }

        CalculateResponse resp = new CalculateResponse();
        resp.setResult(result);

        return resp;
    }
}
