package nl.ead.webservice.dao;

import nl.ead.webservice.model.User;
import nl.ead.webservice.model.UserBitcoinInfo;
import nl.ead.webservice.model.UserPayPalInfo;

/**
 * Created by Joost on 21/03/2016.
 */
public interface IPersistenceConnector {
    void saveUserBitcoinInfo(UserBitcoinInfo info);
    void saveUserPaypalInfo(UserPayPalInfo info);
    void savePaymentLog(Long userID,String logDetails);
    Long getUserIDByName(String username);
}
