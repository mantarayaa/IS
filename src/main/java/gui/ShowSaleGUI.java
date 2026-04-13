package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import domain.*;
import java.awt.*;

public class ShowSaleGUI extends JFrame {
	private DefaultListModel<Offer> offerListModel = new DefaultListModel<>();
	private JList<Offer> offerList = new JList<>(offerListModel);
	private JTextField fieldMakeOffer = new JTextField();
	private JLabel lblVendido;

	public ShowSaleGUI(Sale sale, String currentUserEmail, QuerySalesGUI parent) {
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(650, 450));
		this.setTitle("Detalle: " + sale.getTitle());
		this.setLocationRelativeTo(null);

		JLabel lblInfo = new JLabel("Producto: " + sale.getTitle() + " - " + sale.getPrice() + "€");
		lblInfo.setBounds(20, 20, 400, 20);
		getContentPane().add(lblInfo);

		BLFacade facade = MainGUI.getBusinessLogic();
		
		JScrollPane scroll = new JScrollPane(offerList);
		scroll.setBounds(20, 200, 250, 150);
		if (sale.getOffers() != null) {
			for (Offer o : sale.getOffers()) offerListModel.addElement(o);
		}
		getContentPane().add(scroll);

		boolean esDuenio = sale.getSeller().getEmail().equals(currentUserEmail);
		JButton btnAction = new JButton(esDuenio ? "Aceptar Oferta" : "Hacer Oferta");
		btnAction.setBounds(300, 250, 250, 30);
		
		lblVendido = new JLabel("");
		lblVendido.setForeground(new Color(0, 102, 0));
		lblVendido.setBounds(280, 200, 400, 20);
		getContentPane().add(lblVendido);

		if (sale.getStatus() == 4) {
			lblVendido.setText("VENDIDO A: " + sale.getBuyerEmail() + " POR " + sale.getPrice() + "€");
			btnAction.setEnabled(false);
		} else {
			if (!esDuenio) {
				fieldMakeOffer.setBounds(300, 220, 100, 25);
				getContentPane().add(fieldMakeOffer);
			}

			btnAction.addActionListener(e -> {
				if (esDuenio) {
					Offer o = offerList.getSelectedValue();
					if (o != null) {
						boolean exito = facade.acceptOffer(o.getBuyerEmail(), sale, o.getAmount());
						if (exito) {
							lblVendido.setText("VENDIDO A: " + o.getBuyerEmail() + " POR " + o.getAmount() + "€");
							btnAction.setEnabled(false);
							
							MainGUI.actualizarSaldo();
							
							if (parent != null) parent.refrescar();
							
							JOptionPane.showMessageDialog(this, "¡Venta completada!");
						} else {
							JOptionPane.showMessageDialog(this, "Error: Saldo insuficiente del comprador.");
						}
					}
				} else {
					try {
						float val = Float.parseFloat(fieldMakeOffer.getText());
						User u = facade.getUser(currentUserEmail);
						if (val > u.getBalance()) {
							JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
						} else {
							facade.createOffer(sale, currentUserEmail, val);
							offerListModel.addElement(new Offer(val, currentUserEmail, sale));
							fieldMakeOffer.setText("");
						}
					} catch (Exception ex) { JOptionPane.showMessageDialog(this, "Cantidad no válida."); }
				}
			});
		}
		getContentPane().add(btnAction);

		JButton btnClose = new JButton("Cerrar");
		btnClose.setBounds(480, 370, 100, 30);
		btnClose.addActionListener(e -> this.setVisible(false));
		getContentPane().add(btnClose);
	}
}