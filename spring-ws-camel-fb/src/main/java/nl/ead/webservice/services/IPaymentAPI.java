package nl.ead.webservice.services;

/**
 * Created by Joost on 14/03/2016.
 */
public interface IPaymentAPI {

    public boolean doPayment(double subscriptionCost);
}
