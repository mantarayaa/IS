package domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
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
    private int quantity; 

    @ManyToOne
    private Sale sale;

    public Offer() { super(); }

    public Offer(float amount, String buyerEmail, Sale sale, int quantity) {
        this.amount = amount;
        this.buyerEmail = buyerEmail;
        this.sale = sale;
        this.quantity = quantity;
    }

    public Integer getOfferNumber() { return offerNumber; }
    public float getAmount() { return amount; }
    public String getBuyerEmail() { return buyerEmail; }
    public int getQuantity() { return quantity; }
    
    @Override
    public String toString() {
        return buyerEmail + ": " + amount + "€ x " + quantity + " uds.";
    }
}