package nl.ead.webservice.services;

import com.paypal.api.payments.*;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;
import nl.ead.webservice.FundingInstruments;


import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Joost on 14/03/2016.
 */
public class PayPalEndpoint implements IPaymentAPI {

    private FundingInstruments data;

    private String client;
    private String secret;
    private String intent;
    private String currency;
    private String paymentMethod;

    public PayPalEndpoint(FundingInstruments data) {
        this.data = data;

        client = "ATpVSEOocz9uTKEYPc0_EWnDZ40KteME2AL-hn8q1lSyDIjvC602CeUUqsyBO6lfExIjCxmozbNB4lVx";
        secret = "EORO0WsW3hM_ikaMqZ823cHjXKpgJr4hfp_IBPEh27-TjEreppLCt8NDShqEyxtUMvvQvFrxRiwup0Vw";
        intent = "sale";
        currency = "USD";
        paymentMethod = "credit_card";
    }

    public boolean doPayment(double subscriptionCost) {
        return createPayPalPayment(subscriptionCost);
    }

    boolean createPayPalPayment(double subscriptionCost) {
        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");
        String accessToken = null;

        try {
            accessToken = new OAuthTokenCredential(client, secret, sdkConfig).getAccessToken();

            System.out.println(accessToken.toString());

            APIContext apiContext = new APIContext(accessToken);
            apiContext.setConfigurationMap(sdkConfig);

            CreditCard creditCard = new CreditCard();
            creditCard.setType(data.getCreditcardType().get(0));                //            creditCard.setType("visa");
            creditCard.setNumber(data.getCreditcardNumber().get(0));            //            creditCard.setNumber("4446283280247004");
            creditCard.setExpireMonth(data.getExpireMonth().get(0).toString()); //            creditCard.setExpireMonth("11");
            creditCard.setExpireYear(data.getExpireYear().get(0).toString());   //            creditCard.setExpireYear("2018");
            creditCard.setFirstName(data.getFirstName().get(0));                //            creditCard.setFirstName("Joe");
            creditCard.setLastName(data.getLastName().get(0));                  //            creditCard.setLastName("Test");

            FundingInstrument fundingInstrument = new FundingInstrument();
            fundingInstrument.setCreditCard(creditCard);

            List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
            fundingInstrumentList.add(fundingInstrument);

            Payer payer = new Payer();
            payer.setFundingInstruments(fundingInstrumentList);
            payer.setPaymentMethod(paymentMethod);

            System.out.println(subscriptionCost);
            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(Double.toString(subscriptionCost));                 //              amount.setTotal("12.02");

            Transaction transaction = new Transaction();
            transaction.setDescription("creating a direct payment with credit card");
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<Transaction>();
            transactions.add(transaction);

            Payment payment = new Payment();
            payment.setIntent(intent);
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            Payment createdPayment = payment.create(apiContext);

            if (createdPayment.getState().equals("approved")) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
