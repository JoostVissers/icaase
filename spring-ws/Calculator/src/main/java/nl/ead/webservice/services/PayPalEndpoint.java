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
public class PayPalEndpoint implements IPaymentAPI{

    private FundingInstruments data;
    private String payPalHttpsURL;

    public PayPalEndpoint(FundingInstruments data) {
        this.data = data;
        payPalHttpsURL = "https://api.sandbox.paypal.com/v1/payments/payment";
    }

    public boolean doPayment(float paymentAmount){

        try{
            createPayPalPayment();
        }
        catch (Exception e){

        }

        return false;
    }

    void createPayPalPayment(){


        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");
        String accessToken = null;

        try{
            accessToken = new OAuthTokenCredential("ATpVSEOocz9uTKEYPc0_EWnDZ40KteME2AL-hn8q1lSyDIjvC602CeUUqsyBO6lfExIjCxmozbNB4lVx", "EORO0WsW3hM_ikaMqZ823cHjXKpgJr4hfp_IBPEh27-TjEreppLCt8NDShqEyxtUMvvQvFrxRiwup0Vw", sdkConfig).getAccessToken();
        }
        catch (Exception e){

        }

        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(sdkConfig);

        CreditCard creditCard = new CreditCard();
        creditCard.setType("visa");
        creditCard.setNumber("4417119669820331");
        creditCard.setExpireMonth("11");
        creditCard.setExpireYear("2018");
        creditCard.setFirstName("loL");
        creditCard.setLastName("dERP");

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);

        List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
        fundingInstrumentList.add(fundingInstrument);

        Payer payer = new Payer();
        payer.setFundingInstruments(fundingInstrumentList);
        payer.setPaymentMethod("credit_card");

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal("10.02");

        Transaction transaction = new Transaction();
        transaction.setDescription("creating a direct payment with credit card");
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        try{
            Payment createdPayment = payment.create(apiContext);

            System.out.println(createdPayment.getState());
        }
        catch (Exception e){

        }
    }


}
