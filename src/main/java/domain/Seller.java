package domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

	public Seller() {
		super();
	}

	public Seller(String email, String name, String password) {
		super(email, name, password);
	}
	
	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public String toString(){
		return super.getEmail() + ";" + super.getName() + sales;
	}
	
	public Sale addSale(String title, String description, int status, float price,  Date pubDate, File file)  {
		Sale sale=new Sale(title, description, status, price,  pubDate, file, this);
        sales.add(sale);
        return sale;
	}

	public boolean doesSaleExist(String title)  {	
		for (Sale s:sales)
			if ( s.getTitle().compareTo(title)==0 )
			 return true;
		return false;
	}
	
	@XmlIDREF
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Sale> boughtSales = new ArrayList<Sale>(); 

	public List<Sale> getBoughtSales() { return boughtSales; }

	public void addBoughtSale(Sale sale) {
	    this.boughtSales.add(sale);
	}
}