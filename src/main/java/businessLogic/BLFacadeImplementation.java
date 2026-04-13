package businessLogic;

import java.io.File;
import java.util.Date;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;
import dataAccess.DataAccess;
import domain.Sale;
import domain.Offer;
import exceptions.*;
import java.awt.Image;

@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation implements BLFacade {
	private DataAccess dbManager;

	public BLFacadeImplementation() {
		dbManager = new DataAccess();
	}

	public BLFacadeImplementation(DataAccess da) {
		dbManager = da;
	}

	@WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file, boolean sinImagen) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dbManager.open();
		Sale product = dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file, sinImagen);
		dbManager.close();
		return product;
	}

	@WebMethod
	public List<Sale> getSales(String desc) {
		dbManager.open();
		List<Sale> sales = dbManager.getSales(desc);
		dbManager.close();
		return sales;
	}

	@WebMethod
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		dbManager.open();
		List<Sale> sales = dbManager.getPublishedSales(desc, pubDate);
		dbManager.close();
		return sales;
	}

	@WebMethod
	public void initializeBD() {
		dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}

	@WebMethod
	public Image downloadImage(String imageName) {
		dbManager.open();
		Image img = dbManager.downloadImage(imageName);
		dbManager.close();
		return img;
	}

	public domain.User doLogin(String email, String password) {
		dbManager.open();
		domain.User u = dbManager.doLogin(email, password);
		dbManager.close();
		return u;
	}

	public boolean registerUser(String email, String name, String password, boolean isSeller) {
		dbManager.open();
		boolean res = dbManager.registerUser(email, name, password, isSeller);
		dbManager.close();
		return res;
	}

	@WebMethod
	public boolean acceptOffer(String buyerEmail, Sale sale, float amount) {
	    dbManager.open();
	    boolean res = dbManager.acceptOffer(buyerEmail, sale, amount);
	    dbManager.close();
	    return res;
	}
	
	public List<Sale> getBoughtSales(String email) {
		dbManager.open();
		List<Sale> bought = dbManager.getBoughtSales(email);
		dbManager.close();
		return bought;
	}

	@WebMethod
	public void createOffer(Sale sale, String buyerEmail, float amount) {
		dbManager.open();
		dbManager.createOffer(sale, buyerEmail, amount);
		dbManager.close();
	}

	@WebMethod
	public List<Sale> getSellerSales(String email) {
		dbManager.open();
		List<Sale> sales = dbManager.getSellerSales(email);
		dbManager.close();
		return sales;
	}

	@WebMethod
	public float updateUserBalance(String email, float amount) {
		dbManager.open();
		float newBalance = dbManager.updateUserBalance(email, amount);
		dbManager.close();
		return newBalance;
	}
	
	@WebMethod
	public domain.User getUser(String email) {
	    dbManager.open();
	    domain.User u = dbManager.getUser(email);
	    dbManager.close();
	    return u;
	}
	
	
}