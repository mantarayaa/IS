package domain;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
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
	private int  status;
	private float price;
	private Date pubDate;
	private String fileName;
	private String buyerEmail; // Necesario para el historial de compras
	
	private Seller seller;  

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Offer> offers = new ArrayList<Offer>();
	
	public Sale(){
		super();
	}
		
	public Sale(String title, String description, int status, float price, Date pubDate, File file, Seller seller) {
		super();

		this.title = title;
		this.description = description;
		this.status = status;
		this.price=price;
		this.pubDate=pubDate;
		if (file!=null) {
		    this.fileName=file.getName();
			try {
				BufferedImage img1 = ImageIO.read(file);
				String path="src/main/resources/images/";
				File outputfile = new File(path+file.getName());
			    ImageIO.write(img1, "png", outputfile); 

			} catch(IOException ex) {
				// Error al escribir la imagen
		    }
		}
		this.seller = seller;
	}
	
	public Integer getSaleNumber() {
		return saleNumber;
	}

	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Date getPublicationDate() {
		return pubDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.pubDate = publicationDate;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getFile() {
		return fileName;
	}
	
	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void addOffer(float amount, String buyerEmail) {
		Offer offer = new Offer(amount, buyerEmail, this);
		this.offers.add(offer);
	}
	
	public String toString(){
		return saleNumber+";"+title+";"+price;  
	}
}