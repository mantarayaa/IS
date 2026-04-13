package businessLogic;

import java.io.File;
import java.util.Date;
import java.util.List;
import domain.Sale;
import domain.Offer;
import exceptions.*;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.Image;

@WebService
public interface BLFacade {
	@WebMethod public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file, boolean sinImagen) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;
	@WebMethod public List<Sale> getSales(String desc);
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);
	@WebMethod public void initializeBD();
	@WebMethod public Image downloadImage(String imageName);
	@WebMethod public domain.User doLogin(String email, String password);
	@WebMethod public boolean registerUser(String email, String name, String password, boolean isSeller);
	@WebMethod public boolean acceptOffer(String buyerEmail, Sale sale, float amount);
	@WebMethod public List<Sale> getBoughtSales(String email);
	@WebMethod public void createOffer(Sale sale, String buyerEmail, float amount);
	@WebMethod public List<Sale> getSellerSales(String email);
	
	@WebMethod public float updateUserBalance(String email, float amount);
	@WebMethod public domain.User getUser(String email);
	
}