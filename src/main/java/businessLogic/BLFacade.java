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
	@WebMethod public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file, boolean sinImagen, int stock) throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;
	@WebMethod public List<Sale> getSales(String desc);
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);
	@WebMethod public void initializeBD();
	@WebMethod public Image downloadImage(String imageName);
	@WebMethod public domain.User doLogin(String email, String password);
	@WebMethod public boolean registerUser(String email, String name, String password, boolean isSeller);
	
	// [ACTUALIZADO]: Ahora recibe el objeto Offer completo para procesar stock
	
	@WebMethod public List<Sale> getBoughtSales(String email);
	
	// [ACTUALIZADO]: Ahora recibe la cantidad (int)
	
	@WebMethod public List<Sale> getSellerSales(String email);
	@WebMethod public float updateUserBalance(String email, float amount);
	@WebMethod public domain.User getUser(String email);
	@WebMethod public boolean rateSeller(int saleNumber, int score, String comment);
	@WebMethod public boolean toggleFavorite(String email, int saleNumber);
	@WebMethod public boolean buySale(String buyerEmail, int saleNumber, int quantity);
	
	@WebMethod public boolean acceptOffer(String buyerEmail, Sale sale, Offer offer);
	@WebMethod public void createOffer(Sale sale, String buyerEmail, float amount, int quantity);
}