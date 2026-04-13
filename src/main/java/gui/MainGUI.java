package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import domain.User;
import java.awt.*;

public class MainGUI extends JFrame {
	private String userMail;
	private boolean isSellerMode; 
	private static JLabel lblBalance; 
	private static BLFacade appFacadeInterface;
	private static String staticMail; 

	public static BLFacade getBusinessLogic() { return appFacadeInterface; }
	public static void setBussinessLogic(BLFacade facade) { appFacadeInterface = facade; }

	public static void actualizarSaldo() {
		if (lblBalance != null && appFacadeInterface != null) {
			User u = appFacadeInterface.getUser(staticMail);
			if (u != null) {
				lblBalance.setText("Saldo: " + u.getBalance() + "€");
			}
		}
	}

	public MainGUI(String mail, boolean isSeller) {
		this.userMail = mail;
		staticMail = mail;
		this.isSellerMode = isSeller;
		
		this.setSize(500, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblBalance = new JLabel();
		actualizarSaldo(); 
		lblBalance.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JButton btnRecharge = new JButton("Recargar");
		btnRecharge.addActionListener(e -> {
			String input = JOptionPane.showInputDialog(this, "¿Cuánto dinero quieres añadir?");
			try {
				if (input != null) {
					float amount = Float.parseFloat(input);
					if (amount <= 0) throw new Exception();
					appFacadeInterface.updateUserBalance(userMail, amount);
					actualizarSaldo(); 
					JOptionPane.showMessageDialog(this, "¡Saldo actualizado!");
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Introduce una cantidad válida.");
			}
		});
		
		panelTop.add(lblBalance);
		panelTop.add(btnRecharge);

		JButton btnCreate = new JButton("Crear Venta");
		btnCreate.setEnabled(isSellerMode);
		btnCreate.addActionListener(e -> new CreateSaleGUI(userMail).setVisible(true));

		JButton btnQuery = new JButton("Buscar Productos");
		btnQuery.addActionListener(e -> new QuerySalesGUI(userMail).setVisible(true));

		JButton btnExtra = new JButton(isSellerMode ? "Ver mis ventas" : "Ver mis compras");
		btnExtra.addActionListener(e -> {
			QuerySalesGUI q = new QuerySalesGUI(userMail);
			if (isSellerMode) q.setModoVendedor(true); else q.setModoHistorial(true);
			q.setVisible(true);
		});

		JPanel jContentPane = new JPanel(new GridLayout(4, 1, 10, 10));
		jContentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		jContentPane.add(panelTop);
		jContentPane.add(btnCreate);
		jContentPane.add(btnQuery);
		jContentPane.add(btnExtra);

		this.setContentPane(jContentPane);
		this.setTitle("Market - " + userMail + (isSellerMode ? " (Vendedor)" : " (Comprador)"));
	}
}