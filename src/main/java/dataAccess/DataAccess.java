package dataAccess;

import java.awt.Image;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.persistence.*;
import configuration.ConfigXML;
import configuration.UtilDate;
import domain.*;
import exceptions.*;

public class DataAccess {
	private EntityManager db;
	private EntityManagerFactory emf;
	ConfigXML c = ConfigXML.getInstance();

	public DataAccess() {
		if (c.isDatabaseInitialized()) {
			String fileName = c.getDbFilename();
			if (!c.isDatabaseLocal()) fileName = "src/main/resources/db/" + fileName;
			File fileToDelete = new File(fileName);
			if (fileToDelete.delete()) { new File(fileName + "$").delete(); }
		}
		open();
		if (c.isDatabaseInitialized()) initializeDB();
	}

	public void open() {
		String fileName = c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("javax.persistence.jdbc.user", c.getUser());
			properties.put("javax.persistence.jdbc.password", c.getPassword());
			emf = Persistence.createEntityManagerFactory("objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);
			db = emf.createEntityManager();
		}
	}

	public void createOffer(Sale sale, String buyerEmail, float amount, int quantity) {
	    db.getTransaction().begin();
	    try {
	        Sale s = db.find(Sale.class, sale.getSaleNumber());
	        Offer off = new Offer(amount, buyerEmail, s, quantity);
	        s.getOffers().add(off);
	        db.getTransaction().commit();
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback();
	    }
	}

	public boolean acceptOffer(String buyerEmail, Sale sale, Offer offer) {
	    db.getTransaction().begin();
	    try {
	        User buyer = db.find(User.class, buyerEmail);
	        Sale s = db.find(Sale.class, sale.getSaleNumber());
	        Seller seller = db.find(Seller.class, s.getSeller().getEmail());
	        Offer o = db.find(Offer.class, offer.getOfferNumber());

	        float total = o.getAmount() * o.getQuantity();

	        if (s.getStock() < o.getQuantity() || buyer.getBalance() < total) {
	            db.getTransaction().rollback();
	            return false;
	        }

	        buyer.setBalance(buyer.getBalance() - total);
	        seller.setBalance(seller.getBalance() + total);

	        s.setStock(s.getStock() - o.getQuantity());
	        if (s.getStock() == 0) {
	            s.setStatus(4);
	            s.setBuyerEmail(buyerEmail);
	            s.setPrice(o.getAmount());
	        }
	        
	        ((Seller)buyer).addBoughtSale(s);
	        s.getOffers().remove(o);

	        db.getTransaction().commit();
	        return true;
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback();
	        return false;
	    }
	}

	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file, boolean sinImagen, int stock) throws MustBeLaterThanTodayException {
		if (pubDate.before(UtilDate.trim(new Date()))) throw new MustBeLaterThanTodayException("Date error");
		db.getTransaction().begin();
		Seller seller = db.find(Seller.class, sellerEmail);
		Sale sale = seller.addSale(title, description, status, price, pubDate, file);
		sale.setStock(stock);
		db.persist(seller);
		db.getTransaction().commit();
		return sale;
	}

	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <= ?2 AND s.status != 4", Sale.class);
		query.setParameter(1, "%" + desc + "%");
		query.setParameter(2, pubDate);
		return query.getResultList();
	}

	public User doLogin(String email, String password) {
		User user = db.find(User.class, email);
		if (user != null && user.getPassword().equals(password)) return user;
		return null;
	}

	public User getUser(String email) { return db.find(User.class, email); }

	public boolean registerUser(String email, String name, String password, boolean isSeller) {
		db.getTransaction().begin();
		try {
			if (db.find(User.class, email) != null) { db.getTransaction().commit(); return false; }
			Seller newUser = new Seller(email, name, password);
			db.persist(newUser);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) { if (db.getTransaction().isActive()) db.getTransaction().rollback(); return false; }
	}
	
	public float updateUserBalance(String email, float amount) {
		db.getTransaction().begin();
		try {
			User u = db.find(User.class, email);
			u.setBalance(u.getBalance() + amount);
			float res = u.getBalance();
			db.getTransaction().commit();
			return res;
		} catch (Exception e) { if (db.getTransaction().isActive()) db.getTransaction().rollback(); return -1; }
	}

	public List<Sale> getBoughtSales(String email) {
		Seller user = db.find(Seller.class, email);
		return (user != null) ? user.getBoughtSales() : new ArrayList<Sale>();
	}

	public List<Sale> getSellerSales(String email) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.seller.email = ?1", Sale.class);
		query.setParameter(1, email);
		return query.getResultList();
	}

	public List<Sale> getSales(String desc) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1", Sale.class);
		query.setParameter(1, "%" + desc + "%");
		return query.getResultList();
	}

	public boolean toggleFavorite(String email, int saleNumber) {
		db.getTransaction().begin();
		try {
			User u = db.find(User.class, email);
			Sale s = db.find(Sale.class, saleNumber);
			if (u.getFavorites().contains(s)) u.getFavorites().remove(s);
			else u.getFavorites().add(s);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) { if (db.getTransaction().isActive()) db.getTransaction().rollback(); return false; }
	}

	public boolean rateSeller(int saleNumber, int score, String comment) {
		db.getTransaction().begin();
		try {
			Sale s = db.find(Sale.class, saleNumber);
			if (s == null || s.isRated()) { db.getTransaction().rollback(); return false; }
			Seller seller = db.find(Seller.class, s.getSeller().getEmail());
			Review r = new Review(score, comment, s.getBuyerEmail());
			seller.addReview(r);
			s.setRated(true);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) { if (db.getTransaction().isActive()) db.getTransaction().rollback(); return false; }
	}

	public boolean buySale(String buyerEmail, int saleNumber, int quantity) {
		db.getTransaction().begin();
		try {
			User buyer = db.find(User.class, buyerEmail);
			Sale s = db.find(Sale.class, saleNumber);
			float total = s.getPrice() * quantity;
			if (s.getStock() < quantity || buyer.getBalance() < total) { db.getTransaction().rollback(); return false; }
			Seller seller = db.find(Seller.class, s.getSeller().getEmail());
			buyer.setBalance(buyer.getBalance() - total);
			seller.setBalance(seller.getBalance() + total);
			s.setStock(s.getStock() - quantity);
			if (s.getStock() == 0) s.setStatus(4);
			((Seller)buyer).addBoughtSale(s);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) { if (db.getTransaction().isActive()) db.getTransaction().rollback(); return false; }
	}

	public Image downloadImage(String imageName) { try { return ImageIO.read(new File("src/main/resources/images/" + imageName)); } catch (Exception e) { return null; } }
	public void close() { if (db != null && db.isOpen()) db.close(); }
	public void initializeDB() {} 
}