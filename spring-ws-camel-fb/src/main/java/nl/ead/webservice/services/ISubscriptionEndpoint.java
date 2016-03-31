package nl.ead.webservice.services;

import nl.han.dare2date.matchservice.model.CalculateRequest;
import nl.han.dare2date.matchservice.model.CalculateResponse;

/**
 * Created by martijn on 2016-03-24.
 */
public interface ISubscriptionEndpoint {
    CalculateResponse pay(CalculateRequest req);
}
