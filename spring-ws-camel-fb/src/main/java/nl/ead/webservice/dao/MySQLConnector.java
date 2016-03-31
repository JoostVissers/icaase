package nl.ead.webservice.dao;

import nl.ead.webservice.model.PaymentLog;
import nl.ead.webservice.model.User;

import nl.ead.webservice.model.UserBitcoinInfo;
import nl.ead.webservice.model.UserPayPalInfo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

/**
 * Created by Joost on 21/03/2016.
 */

@Repository
public class MySQLConnector implements IPersistenceConnector {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveUserBitcoinInfo(UserBitcoinInfo info) {

    }

    @Transactional
    public void saveUserPaypalInfo(UserPayPalInfo info) {
        em.persist(info);
    }

    @Transactional
    public void savePaymentLog(Long userID, String logDetails) {

        PaymentLog pl = new PaymentLog(userID, logDetails);
        em.persist(pl);
    }

    @Transactional
    public Long getUserIDByName(String username){
        createNewUsers();
        String hql = "FROM User WHERE username = '"+ username +"'";
        Query query = em.createQuery(hql);

        List results = query.getResultList();
        Long uid = Long.parseLong("0");

        for(Object us : results){
            uid = ((User)us).getId();
        }
        System.out.println("==============================---------------------------------------------++++++++++++++++++++++++++++");
        System.out.println(uid);
        return uid;
    }

    @Transactional
    private void createNewUsers() {
        User u1 = new User("Frits");
        User u2 = new User("Martijn");
        em.persist(u1);
        em.persist(u2);
    }
}



