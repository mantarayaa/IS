package domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Offer implements Serializable {
    @XmlID
    @Id
    @GeneratedValue
    @XmlJavaTypeAdapter(IntegerAdapter.class)
    private Integer offerNumber;
    
    private float amount;
    private String buyerEmail;
    
    @ManyToOne
    private Sale sale;

    public Offer() {
        super();
    }

    public Offer(float amount, String buyerEmail, Sale sale) {
        this.amount = amount;
        this.buyerEmail = buyerEmail;
        this.sale = sale;
    }

    public Integer getOfferNumber() {
        return offerNumber;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
    
    @Override
    public String toString() {
        return buyerEmail + ": " + amount + "€";
    }
}