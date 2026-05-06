package domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Seller extends User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlIDREF
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Sale> sales=new ArrayList<Sale>();

	@XmlIDREF
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Sale> boughtSales = new ArrayList<Sale>(); 

    // Reputación del vendedor
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
    private float averageRating = 0.0f;
    private int numVotes = 0;

	public Seller() { super(); }
	public Seller(String email, String name, String password) { super(email, name, password); }
	
	public List<Sale> getSales() { return sales; }
	public void setSales(List<Sale> sales) { this.sales = sales; }
	public void addBoughtSale(Sale sale) { this.boughtSales.add(sale); }
	public List<Sale> getBoughtSales() { return boughtSales; }

    public void addReview(Review r) {
        this.reviews.add(r);
        this.averageRating = ((this.averageRating * this.numVotes) + r.getScore()) / (this.numVotes + 1);
        this.numVotes++;
    }

    public List<Review> getReviews() { return reviews; }
    public float getAverageRating() { return averageRating; }
    public int getNumVotes() { return numVotes; }

	public Sale addSale(String title, String description, int status, float price, java.util.Date pubDate, File file) {
		Sale sale=new Sale(title, description, status, price, pubDate, file, this);
        sales.add(sale);
        return sale;
	}

	public String toString(){
		return super.getEmail() + ";" + super.getName() + sales;
	}
}