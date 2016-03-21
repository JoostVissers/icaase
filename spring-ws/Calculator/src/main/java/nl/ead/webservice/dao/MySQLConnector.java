package nl.ead.webservice.dao;

import nl.ead.webservice.model.PaymentLog;
import nl.ead.webservice.model.User;

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
    @Override
    public void saveUserPaymentInfo(User user) {

    }

    @Transactional
    @Override
    public void savePaymentLog(String userName, String logDetails) {
        createNewUsers();

        PaymentLog pl = new PaymentLog(getUserIDByName(userName), logDetails);
        em.persist(pl);
    }

    private Long getUserIDByName(String username){
        String hql = "FROM User WHERE username = '"+ username +"'";
        Query query = em.createQuery(hql);

        List results = query.getResultList();
        Long uid = Long.parseLong("0");

        for(Object us : results){
            uid = ((User)us).getId();
        }

        return uid;
    }

    private void createNewUsers() {
        User u1 = new User("Joost");
        User u2 = new User("Martijn");
        em.persist(u1);
        em.persist(u2);
    }
}



