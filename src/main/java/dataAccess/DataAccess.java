package dataAccess;

import java.awt.Image;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.persistence.*;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Seller;
import domain.Sale;
import domain.Offer;
import domain.User; // ESTE IMPORT ES EL QUE TE DABA EL ERROR
import exceptions.*;

public class DataAccess {
	private EntityManager db;
	private EntityManagerFactory emf;
	private static final String basePath = "src/main/resources/images/";
	private static final String dbServerDir = "src/main/resources/db/";
	ConfigXML c = ConfigXML.getInstance();

	public DataAccess() {
		if (c.isDatabaseInitialized()) {
			String fileName = c.getDbFilename();
			if (!c.isDatabaseLocal()) fileName = dbServerDir + fileName;
			File fileToDelete = new File(fileName);
			if (fileToDelete.delete()) {
				new File(fileName + "$").delete();
			}
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

	public void initializeDB() {
		db.getTransaction().begin();
		try {
			Seller s = db.find(Seller.class, "seller1@gmail.com");
			if (s == null) {
				Seller seller1 = new Seller("seller1@gmail.com", "Aitor Fernandez", "1234");
				db.persist(seller1);
				db.getTransaction().commit();
			} else {
				db.getTransaction().rollback();
			}
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
		}
	}

	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file, boolean sinImagen) throws MustBeLaterThanTodayException {
		if (pubDate.before(UtilDate.trim(new Date()))) throw new MustBeLaterThanTodayException("Date error");
		db.getTransaction().begin();
		Seller seller = db.find(Seller.class, sellerEmail);
		Sale sale = seller.addSale(title, description, status, price, pubDate, file);
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

	public List<Sale> getSellerSales(String email) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.seller.email = ?1", Sale.class);
		query.setParameter(1, email);
		return query.getResultList();
	}

	public void createOffer(Sale sale, String buyerEmail, float amount) {
		db.getTransaction().begin();
		try {
			Sale s = db.find(Sale.class, sale.getSaleNumber());
			s.addOffer(amount, buyerEmail);
			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
		}
	}

	public User doLogin(String email, String password) {
		User user = db.find(User.class, email);
		if (user != null && user.getPassword().equals(password)) return user;
		return null;
	}

	public User getUser(String email) {
		return db.find(User.class, email);
	}

	public boolean registerUser(String email, String name, String password, boolean isSeller) {
		db.getTransaction().begin();
		try {
			if (db.find(User.class, email) != null) {
				db.getTransaction().commit();
				return false;
			}
			Seller newUser = new Seller(email, name, password);
			db.persist(newUser);
			db.getTransaction().commit();
			return true;
		} catch (Exception e) { 
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			return false; 
		}
	}

	public boolean acceptOffer(String buyerEmail, Sale sale, float amount) {
	    db.getTransaction().begin();
	    try {
	        User buyer = db.find(User.class, buyerEmail);
	        if (buyer == null || buyer.getBalance() < amount) {
	            db.getTransaction().rollback();
	            return false;
	        }

	        Sale s = db.find(Sale.class, sale.getSaleNumber());
	        Seller seller = db.find(Seller.class, s.getSeller().getEmail());

	        buyer.setBalance(buyer.getBalance() - amount);
	        seller.setBalance(seller.getBalance() + amount);

	        ((Seller)buyer).addBoughtSale(s);
	        s.setStatus(4); 
	        s.setBuyerEmail(buyerEmail);
	        s.setPrice(amount);

	        db.getTransaction().commit();
	        return true;
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback();
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public float updateUserBalance(String email, float amount) {
		db.getTransaction().begin();
		try {
			User u = db.find(User.class, email);
			u.setBalance(u.getBalance() + amount);
			float res = u.getBalance();
			db.getTransaction().commit();
			return res;
		} catch (Exception e) { 
			if (db.getTransaction().isActive()) db.getTransaction().rollback();
			return -1;
		}
	}

	public List<Sale> getBoughtSales(String email) {
		Seller user = db.find(Seller.class, email);
		return (user != null) ? user.getBoughtSales() : new ArrayList<Sale>();
	}

	public List<Sale> getSales(String desc) {
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1", Sale.class);
		query.setParameter(1, "%" + desc + "%");
		return query.getResultList();
	}

	public Image downloadImage(String imageName) {
		try { return ImageIO.read(new File(basePath + imageName)); } catch (Exception e) { return null; }
	}

	public void close() { if (db != null && db.isOpen()) db.close(); }
}