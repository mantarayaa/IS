package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;
import domain.Sale;
import domain.Offer;
import java.util.ResourceBundle;

public class ShowSaleGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int baseSize = 160;
    private BufferedImage targetImg;
    public JPanel panel_1;
    private JTextField fieldTitle = new JTextField();
    private JTextField fieldDescription = new JTextField();
    private JTextField fieldPrice = new JTextField();
    private JTextField fieldMakeOffer = new JTextField();
    private JButton btnMakeOffer = new JButton("Hacer Oferta");
    
    // El model es la clave para que la lista se actualice sola
    private DefaultListModel<Offer> offerListModel = new DefaultListModel<>();
    private JList<Offer> offerList = new JList<>(offerListModel);

    public ShowSaleGUI(Sale sale, String currentUserEmail, QuerySalesGUI parent) { 
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(650, 450));
        this.setTitle("Detalle del Producto");

        fieldTitle.setEditable(false);
        fieldTitle.setText(sale.getTitle());
        fieldTitle.setBounds(120, 20, 300, 26);
        getContentPane().add(fieldTitle);

        fieldDescription.setEditable(false);
        fieldDescription.setText(sale.getDescription());
        fieldDescription.setBounds(120, 55, 300, 60);
        getContentPane().add(fieldDescription);

        fieldPrice.setEditable(false);
        fieldPrice.setText(sale.getPrice() + "€");
        fieldPrice.setBounds(120, 125, 80, 20);
        getContentPane().add(fieldPrice);

        // Panel Imagen
        panel_1 = new JPanel();
        panel_1.setBounds(440, 20, 180, 160);
        panel_1.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        getContentPane().add(panel_1);

        BLFacade facade = MainGUI.getBusinessLogic();
        if (sale.getFile() != null) {
            Image img = facade.downloadImage(sale.getFile());
            if (img != null) {
                targetImg = rescale((BufferedImage)img);
                panel_1.setLayout(new BorderLayout());
                panel_1.add(new JLabel(new ImageIcon(targetImg))); 
            }
        }

        // LISTA DE OFERTAS
        JLabel lblOfertas = new JLabel("Ofertas recibidas:");
        lblOfertas.setBounds(20, 200, 150, 20);
        getContentPane().add(lblOfertas);

        JScrollPane scroll = new JScrollPane(offerList);
        scroll.setBounds(20, 225, 250, 100);
        getContentPane().add(scroll);
        
        // Cargar ofertas iniciales
        if (sale.getOffers() != null) {
            for(Offer o : sale.getOffers()) {
                offerListModel.addElement(o);
            }
        }

        // ROL DE USUARIO
        boolean esDuenio = sale.getSeller().getEmail().equals(currentUserEmail);

        if (esDuenio) {
            JLabel lblInfoVendedor = new JLabel("Estás viendo las ofertas de tu producto.");
            lblInfoVendedor.setForeground(Color.BLUE);
            lblInfoVendedor.setBounds(300, 225, 300, 20);
            getContentPane().add(lblInfoVendedor);
        } else {
            JLabel lblTuPrecio = new JLabel("Tu oferta (€):");
            lblTuPrecio.setBounds(300, 225, 100, 20);
            getContentPane().add(lblTuPrecio);

            fieldMakeOffer.setBounds(400, 225, 80, 26);
            getContentPane().add(fieldMakeOffer);

            btnMakeOffer.setBounds(300, 260, 180, 30);
            btnMakeOffer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String textValue = fieldMakeOffer.getText();
                        float val = Float.parseFloat(textValue);
                        if (val <= 0) throw new Exception();

                        // 1. Guardar en la base de datos a través de la lógica de negocio
                        facade.createOffer(sale, currentUserEmail, val);

                        // 2. ACTUALIZACIÓN DIRECTA: Creamos el objeto visual y lo añadimos al modelo
                        // Esto hace que aparezca en la lista sin cerrar la ventana
                        Offer newOffer = new Offer(val, currentUserEmail, sale);
                        offerListModel.addElement(newOffer);

                        // 3. Limpiar el campo y avisar al usuario
                        fieldMakeOffer.setText("");
                        JOptionPane.showMessageDialog(null, "¡Oferta enviada y actualizada!");

                    } catch (Exception ex) { 
                        JOptionPane.showMessageDialog(null, "Por favor, introduce un precio válido."); 
                    }
                }
            });
            getContentPane().add(btnMakeOffer);
        }

        JButton btnClose = new JButton("Cerrar");
        btnClose.setBounds(440, 370, 114, 30);
        btnClose.addActionListener(e -> this.setVisible(false));
        getContentPane().add(btnClose);

        if(sale.getStatus() == 4) {
            btnMakeOffer.setEnabled(false);
            btnMakeOffer.setText("VENDIDO");
        }

        this.setVisible(true);
    }

    public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
}