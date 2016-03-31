package nl.ead.webservice.model;

import nl.ead.webservice.services.Encryptor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Joost on 21/03/2016.
 */
@Entity
public class UserPayPalInfo {
    @Id
    private Long userID;
    private String creditcardNumber;
    private String creditcardType;
    private String expire_month;
    private String expire_year;
    private String cvv2;
    private String first_name;
    private String last_name;
    private String line1;
    private String city;
    private String state;
    private String postal_code;
    private String country_code;

    @Autowired
    public UserPayPalInfo(Long userID, String creditcardNumber, String creditcardType, String expire_month, String expire_year, String cvv2, String first_name, String last_name, String line1, String city, String state, String postal_code, String country_code) {
        Encryptor encryptor = new Encryptor();
        this.userID = userID;
        this.creditcardNumber = encryptor.encrypt(creditcardNumber);
        this.creditcardType = encryptor.encrypt(creditcardType);
        this.expire_month = encryptor.encrypt(expire_month);
        this.expire_year = encryptor.encrypt(expire_year);
        this.cvv2 =  encryptor.encrypt(cvv2);
        this.first_name =  encryptor.encrypt(first_name);
        this.last_name =  encryptor.encrypt(last_name);
        this.line1 =  encryptor.encrypt(line1);
        this.city =  encryptor.encrypt(city);
        this.state =  encryptor.encrypt(state);
        this.postal_code =  encryptor.encrypt(postal_code);
        this.country_code =  encryptor.encrypt(country_code);
    }
}
