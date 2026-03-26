package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUI extends JFrame {
	
    private String sellerMail;
    private boolean isSellerMode; 
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;
	private JButton btnBottomAction = null; 

    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	 
	public static void setBussinessLogic (BLFacade facade){
		appFacadeInterface=facade;
	}

	public MainGUI(String mail, boolean isSeller) {
		super();
		this.sellerMail = mail;
		this.isSellerMode = isSeller; 
		
		this.setSize(495, 350); 
		JLabel jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		jButtonCreateQuery = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonCreateQuery.setEnabled(isSellerMode); 
		jButtonCreateQuery.addActionListener(e -> {
			JFrame a = new CreateSaleGUI(sellerMail);
			a.setVisible(true);
		});
		
		jButtonQueryQueries = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonQueryQueries.addActionListener(e -> {
			QuerySalesGUI a = new QuerySalesGUI(sellerMail);
			a.setVisible(true);
		});

		btnBottomAction = new JButton();
		if (isSellerMode) {
			btnBottomAction.setText("Ver ofertas de mis productos");
			btnBottomAction.addActionListener(e -> {
				QuerySalesGUI v = new QuerySalesGUI(sellerMail);
				v.setModoVendedor(true); 
				v.setVisible(true);
			});
		} else {
			btnBottomAction.setText("Ver mis compras hechas");
			btnBottomAction.addActionListener(e -> {
				QuerySalesGUI v = new QuerySalesGUI(sellerMail);
				v.setModoHistorial(true);
				v.setVisible(true);
			});
		}
		
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridLayout(4, 1, 10, 10));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(btnBottomAction);
		
		setContentPane(jContentPane);
		String modeText = isSellerMode ? " (Vendedor)" : " (Comprador)";
		setTitle("Market: " + sellerMail + modeText);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
}