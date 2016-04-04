package nl.ead.webservice.dao;

import nl.ead.webservice.model.PaymentLog;
import nl.ead.webservice.model.User;

import nl.ead.webservice.model.UserBitcoinInfo;
import nl.ead.webservice.model.UserPayPalInfo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;

/**
 * Created by Joost on 21/03/2016.
 */

@Repository
public class MySQLConnector implements IPersistenceConnector {
    @PersistenceContext
    private EntityManager em;

    public MySQLConnector() {
        this.em = Persistence.createEntityManagerFactory("UsingHibernate").createEntityManager();
    }

    @Transactional
    public void saveUserBitcoinInfo(UserBitcoinInfo info) {

    }

    @Transactional
    public void saveUserPaypalInfo(UserPayPalInfo info) {
        // begin transaction
        em.getTransaction().begin();
        if (!em.contains(info)) {
            em.persist(info);
            em.flush();
        }
        // commit transaction at all
        em.getTransaction().commit();
    }

    @Transactional
    public void savePaymentLog(Long userID, String logDetails) {
        PaymentLog pl = new PaymentLog(userID, logDetails);

        // begin transaction
        em.getTransaction().begin();
        if (!em.contains(pl)) {
            em.persist(pl);
            em.flush();
        }
        // commit transaction at all
        em.getTransaction().commit();

    }

    @Transactional
    public Long getUserIDByName(String username) {
        //createNewUsers();
        String hql = "FROM User WHERE username = '" + username + "'";
        Query query = em.createQuery(hql);

        List results = query.getResultList();
        Long uid = Long.parseLong("0");

        for (Object us : results) {
            uid = ((User) us).getId();
        }
        System.out.println("==============================---------------------------------------------++++++++++++++++++++++++++++");
        System.out.println(uid);
        return uid;
    }

    @Transactional
    public void createNewUsers() {
        User u1 = new User("Frits");
        User u2 = new User("Martijn");
        em.persist(u1);
        em.persist(u2);
    }
}



