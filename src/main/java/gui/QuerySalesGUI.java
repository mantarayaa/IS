package gui;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Sale;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class QuerySalesGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final JLabel jLabelProducts = new JLabel("Productos encontrados:"); 

	private JButton jButtonSearch = new JButton("Buscar"); 
	private JButton jButtonClose = new JButton("Cerrar");
	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts = new JTable();
	private DefaultTableModel tableModelProducts;
	private QuerySalesGUI thisFrame; 
	private JTextField jTextFieldSearch;
	
	private String currentUserEmail;
	private boolean modoHistorial = false; 
	private boolean modoVendedor = false; 

	private String[] columnNamesProducts = new String[] { "Título", "Precio", "Fecha" };

	public QuerySalesGUI(String userEmail) {
		this.currentUserEmail = userEmail;
		thisFrame = this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(600, 450));
		this.setTitle("Buscador de Productos");
		
		jLabelProducts.setBounds(50, 100, 400, 16);
		this.getContentPane().add(jLabelProducts);

		jButtonClose.setBounds(230, 350, 130, 30);
		jButtonClose.addActionListener(e -> thisFrame.setVisible(false));		
		this.getContentPane().add(jButtonClose);

		scrollPanelProducts.setBounds(50, 130, 500, 200);
		scrollPanelProducts.setViewportView(tableProducts);
		
		tableModelProducts = new DefaultTableModel(null, columnNamesProducts) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};
		tableProducts.setModel(tableModelProducts);
		tableModelProducts.setColumnCount(4); 

		this.getContentPane().add(scrollPanelProducts);
		
		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(50, 50, 300, 26);
		getContentPane().add(jTextFieldSearch);
		
		jButtonSearch.setBounds(370, 50, 120, 29);
		jButtonSearch.addActionListener(e -> ejecutarBusqueda());
		getContentPane().add(jButtonSearch);
		
		tableProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2) {
                    int row = tableProducts.getSelectedRow();
                    if (row != -1) {
                        Sale s = (Sale) tableModelProducts.getValueAt(row, 3);
                        // Abrimos el detalle del producto (ShowSaleGUI)
                        ShowSaleGUI detail = new ShowSaleGUI(s, currentUserEmail, thisFrame);
                        detail.setVisible(true);
                    }
                }
            }
        });
	}

	public void setModoHistorial(boolean modo) {
		this.modoHistorial = modo;
		if (modo) {
			this.setTitle("Mis Compras");
			cargarDatos("HISTORIAL");
		}
	}

    public void setModoVendedor(boolean modo) {
        this.modoVendedor = modo;
        if (modo) {
            this.setTitle("Mis Productos y Ofertas");
            cargarDatos("VENDEDOR");
        }
    }

	public void ejecutarBusqueda() {
        cargarDatos("BUSQUEDA");
    }

	private void cargarDatos(String tipo) {
		try {
			tableModelProducts.setDataVector(null, columnNamesProducts);
			tableModelProducts.setColumnCount(4);
			BLFacade facade = MainGUI.getBusinessLogic();
			List<Sale> lista = new ArrayList<>();

            if (tipo.equals("BUSQUEDA")) {
                lista = facade.getPublishedSales(jTextFieldSearch.getText(), UtilDate.trim(new Date()));
            } else if (tipo.equals("HISTORIAL")) {
                lista = facade.getBoughtSales(currentUserEmail);
                jButtonSearch.setEnabled(false);
                jTextFieldSearch.setEnabled(false);
            } else if (tipo.equals("VENDEDOR")) {
                lista = facade.getSellerSales(currentUserEmail);
                jButtonSearch.setEnabled(false);
                jTextFieldSearch.setEnabled(false);
            }

			for (Sale s : lista) {
				Vector<Object> row = new Vector<>();
				row.add(s.getTitle()); 
				row.add(s.getPrice());
				row.add(new SimpleDateFormat("dd/MM/yyyy").format(s.getPublicationDate()));
				row.add(s); 
				tableModelProducts.addRow(row);		
			}
            // Ocultamos la columna del objeto Sale
            tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
}