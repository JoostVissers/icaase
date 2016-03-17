package nl.ead.webservice.services;

import java.util.List;
import nl.ead.webservice.*;
import nl.ead.webservice.dao.ICalculationDao;
import nl.ead.webservice.model.Calculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CalculatorEndpoint {
    private final ICalculationDao calculationDao;
    private final IMoviePrinter moviePrinter;

    @Autowired
    public CalculatorEndpoint(IMoviePrinter moviePrinter, ICalculationDao calculationDao) {
        this.moviePrinter = moviePrinter;
        this.calculationDao = calculationDao;
    }

    @PayloadRoot(localPart = "CalculateRequest", namespace = "http://www.han.nl/schemas/messages")
    @ResponsePayload
    public CalculateResponse calculateSumForName(@RequestPayload CalculateRequest req) {
        // Veranderen naar pay
        // a sequence of a minimum of 1 and unbounded max is generated as a
        // List<>

        SubscriptionMethod method = req.getInput().getSubscriptionMethod();

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
        }
        else if(method.equals(SubscriptionMethod.BITCOIN)){
            //FundingInstruments paramList = req.getInput().getBitcoinParamlist();
        }

//        CalculateOperation op = req.getInput().getOperation();
//        int retValue = paramList.get(0);
//        StringBuffer calculationInput = new StringBuffer();
//        calculationInput.append(paramList.get(0));
//
//        for (int i = 1; i < paramList.size(); i++) {
//            // CalculateOperation is generated as an enum
//            if (op.equals(CalculateOperation.ADD)) {
//                retValue += paramList.get(i).intValue();
//                calculationInput.append(" + " + paramList.get(i).intValue());
//            } else if (op.equals(CalculateOperation.SUBTRACT)) {
//                retValue -= paramList.get(i).intValue();
//                calculationInput.append(" -" + paramList.get(i).intValue());
//            } else if (op.equals(CalculateOperation.MULTIPLY)) {
//                retValue *= paramList.get(i).intValue();
//                calculationInput.append(" * " + paramList.get(i).intValue());
//            } else if (op.equals(CalculateOperation.DIVIDE)) {
//                retValue /= paramList.get(i).intValue();
//                calculationInput.append(" / " + paramList.get(i).intValue());
//            }
//        }



        CalculateResult result = new CalculateResult();
//        result.setMessage("Here are the results of the jury for the calculation " + calculationInput);
//        result.setValue(retValue);
        CalculateResponse resp = new CalculateResponse();
        resp.setResult(result);

        // OK, I know this isn't the best example of an external service for a calculator....
        //moviePrinter.printMovieDetails("Bond");

//         Calculation calculation = new Calculation(calculationInput.toString(),retValue+"");
//        calculationDao.save(calculation);

        return resp;
    }
}
