package gui;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Sale;
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

    private String[] columnNames = new String[] { "Título", "Precio", "Fecha", "Estado", "Objeto" };

    public QuerySalesGUI(String userEmail) {
        this.currentUserEmail = userEmail;
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(700, 450));
        this.setLocationRelativeTo(null);
        this.setTitle("Buscador de Productos");

        jLabelProducts.setBounds(50, 100, 400, 16);
        getContentPane().add(jLabelProducts);

        jTextFieldSearch.setBounds(50, 50, 300, 26);
        getContentPane().add(jTextFieldSearch);

        jButtonSearch.setBounds(370, 50, 120, 29);
        jButtonSearch.addActionListener(e -> {
            tipoActual = modoHistorial ? "HISTORIAL" : (modoVendedor ? "VENDEDOR" : "BUSQUEDA");
            cargarDatos(tipoActual);
        });
        getContentPane().add(jButtonSearch);

        scrollPanelProducts.setBounds(50, 130, 600, 200);
        
        tableModelProducts = new DefaultTableModel(null, columnNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableProducts.setModel(tableModelProducts);
        
        tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(4)); 
        
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
                        Sale s = (Sale) tableModelProducts.getValueAt(row, 4);
                        new ShowSaleGUI(s, currentUserEmail, QuerySalesGUI.this).setVisible(true);
                    }
                }
            }
        });
    }

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
        List<Sale> lista = new ArrayList<>();
        
        if (tipo.equals("BUSQUEDA")) lista = facade.getPublishedSales(jTextFieldSearch.getText(), UtilDate.trim(new Date()));
        else if (tipo.equals("HISTORIAL")) lista = facade.getBoughtSales(currentUserEmail);
        else if (tipo.equals("VENDEDOR")) lista = facade.getSellerSales(currentUserEmail);

        for (Sale s : lista) {
            Vector<Object> row = new Vector<>();
            row.add(s.getTitle());
            row.add(s.getPrice() + "€");
            row.add(new SimpleDateFormat("dd/MM/yyyy").format(s.getPublicationDate()));
            
            row.add(Utils.getStatus(s.getStatus()));
            
            row.add(s); 
            tableModelProducts.addRow(row);
        }
    }
}