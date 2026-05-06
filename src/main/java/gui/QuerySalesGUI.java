package gui;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Sale;
import domain.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class QuerySalesGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JLabel jLabelProducts = new JLabel("Productos encontrados:"); 
	private JButton jButtonSearch = new JButton("Buscar"); 
	private JButton jButtonClose = new JButton("Cerrar");
	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts = new JTable();
	private DefaultTableModel tableModelProducts;
	private JTextField jTextFieldSearch = new JTextField();
	
	private String currentUserEmail;
	private boolean modoHistorial = false; 
	private boolean modoVendedor = false; 
	private String tipoActual = "BUSQUEDA";

	private String[] columnNames = new String[] { "Fav", "Título", "Precio", "Fecha", "Estado", "Objeto" };

	public QuerySalesGUI(String userEmail) {
		this.currentUserEmail = userEmail;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(750, 450));
		this.setLocationRelativeTo(null);
		this.setTitle("Buscador de Productos");

		jLabelProducts.setBounds(50, 100, 400, 16);
		getContentPane().add(jLabelProducts);

		jTextFieldSearch.setBounds(50, 50, 300, 26);
		getContentPane().add(jTextFieldSearch);

		jButtonSearch.setBounds(370, 50, 120, 29);
		jButtonSearch.addActionListener(e -> cargarDatos(tipoActual));
		getContentPane().add(jButtonSearch);

		scrollPanelProducts.setBounds(50, 130, 650, 200);
		
		tableModelProducts = new DefaultTableModel(null, columnNames) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};
		tableProducts.setModel(tableModelProducts);
		
		// Ocultamos la columna del objeto (índice 5)
		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(5)); 
		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(40);
		
		scrollPanelProducts.setViewportView(tableProducts);
		getContentPane().add(scrollPanelProducts);

		jButtonClose.setBounds(280, 350, 130, 30);
		jButtonClose.addActionListener(e -> this.setVisible(false));        
		getContentPane().add(jButtonClose);

		tableProducts.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					int row = tableProducts.getSelectedRow();
					if (row != -1) {
						Sale s = (Sale) tableModelProducts.getValueAt(row, 5);
						new ShowSaleGUI(s, currentUserEmail, QuerySalesGUI.this).setVisible(true);
					}
				}
			}
		});
		
		cargarDatos(tipoActual);
	}

	// --- MÉTODOS QUE TE DABAN ERROR ---
	public void setModoHistorial(boolean modo) {
		this.modoHistorial = modo;
		this.setTitle("Mis Compras");
		jButtonSearch.setVisible(false);
		jTextFieldSearch.setVisible(false);
		tipoActual = "HISTORIAL";
		cargarDatos(tipoActual);
	}

	public void setModoVendedor(boolean modo) {
		this.modoVendedor = modo;
		this.setTitle("Mis Ventas");
		tipoActual = "VENDEDOR";
		cargarDatos(tipoActual);
	}

	public void refrescar() {
		cargarDatos(tipoActual);
	}

	private void cargarDatos(String tipo) {
		tableModelProducts.setRowCount(0);
		BLFacade facade = MainGUI.getBusinessLogic();
		
		User userActual = facade.getUser(currentUserEmail);
		List<Sale> misFavoritos = (userActual != null) ? userActual.getFavorites() : new ArrayList<>();
		
		List<Sale> lista = new ArrayList<>();
		if (tipo.equals("BUSQUEDA")) lista = facade.getPublishedSales(jTextFieldSearch.getText(), UtilDate.trim(new Date()));
		else if (tipo.equals("HISTORIAL")) lista = facade.getBoughtSales(currentUserEmail);
		else if (tipo.equals("VENDEDOR")) lista = facade.getSellerSales(currentUserEmail);

		// Ordenar: Favoritos arriba del todo
		Collections.sort(lista, (s1, s2) -> {
			boolean f1 = misFavoritos.contains(s1);
			boolean f2 = misFavoritos.contains(s2);
			if (f1 && !f2) return -1;
			if (!f1 && f2) return 1;
			return 0;
		});

		for (Sale s : lista) {
			Vector<Object> row = new Vector<>();
			row.add(misFavoritos.contains(s) ? "❤️" : ""); 
			row.add(s.getTitle());
			row.add(s.getPrice() + "€");
			row.add(new SimpleDateFormat("dd/MM/yyyy").format(s.getPublicationDate()));
			row.add(Utils.getStatus(s.getStatus()));
			row.add(s); 
			tableModelProducts.addRow(row);
		}
	}
}