package domain;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Sale implements Serializable {
    @XmlID
    @Id 
    @XmlJavaTypeAdapter(IntegerAdapter.class)
    @GeneratedValue
    private Integer saleNumber;
    private String title;
    private String description;
    private int status;
    private float price;
    private Date pubDate;
    private String fileName;
    private String buyerEmail; 
    private boolean isRated = false; 
    private int stock = 1; // Unidades disponibles
    
    private Seller seller;  

    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Offer> offers = new ArrayList<Offer>();
    
    public Sale(){ super(); }
        
    public Sale(String title, String description, int status, float price, Date pubDate, File file, Seller seller) {
        super();
        this.title = title;
        this.description = description;
        this.status = status;
        this.price = price;
        this.pubDate = pubDate;
        if (file != null) {
            this.fileName = file.getName();
            try {
                BufferedImage img1 = ImageIO.read(file);
                File outputfile = new File("src/main/resources/images/" + file.getName());
                ImageIO.write(img1, "png", outputfile); 
            } catch(IOException ex) { }
        }
        this.seller = seller;
    }
    
    public Integer getSaleNumber() { return saleNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public Date getPublicationDate() { return pubDate; }
    public Seller getSeller() { return seller; }
    public String getFile() { return fileName; }
    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public boolean isRated() { return isRated; }
    public void setRated(boolean rated) { this.isRated = rated; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public List<Offer> getOffers() { return offers; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sale other = (Sale) obj;
        return saleNumber != null && saleNumber.equals(other.saleNumber);
    }

    @Override
    public int hashCode() {
        return saleNumber != null ? saleNumber.hashCode() : 0;
    }
}