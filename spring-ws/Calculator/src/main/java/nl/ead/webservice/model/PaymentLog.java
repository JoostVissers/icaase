package nl.ead.webservice.model;

import com.paypal.api.payments.Payment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Joost on 21/03/2016.
 */
@Entity
public class PaymentLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long logId;


    public PaymentLog(long user, String logText) {
        this.user = user;
        this.logText = logText;
    }

    private long user;
    private String logText;

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
}
