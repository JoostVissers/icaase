package nl.ead.webservice.dao;

import nl.ead.webservice.model.Calculation;
import nl.ead.webservice.model.PaymentLog;
import nl.ead.webservice.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Joost on 21/03/2016.
 */

@Repository
public class MySQLConnector implements  IPersistenceConnector{
    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public void saveUserPaymentInfo(User user) {

    }

    @Transactional
    @Override
    public void savePaymentLog(User user, String logDetails) {

        System.out.println("HOOOOOOOOOOOOOOOOOOOOOI:" + em.find(User.class,user).getId().toString());
        PaymentLog pl = new PaymentLog(em.find(User.class,user).getId(),logDetails);
        em.persist(pl);
    }
}



