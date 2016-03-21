package nl.ead.webservice.dao;

import nl.ead.webservice.model.User;

/**
 * Created by Joost on 21/03/2016.
 */
public interface IPersistenceConnector {
    void saveUserPaymentInfo(User user);
    void savePaymentLog(String user,String logDetails);
}
