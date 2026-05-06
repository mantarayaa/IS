package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import domain.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ShowSaleGUI extends JFrame {
    private DefaultListModel<Offer> offerListModel = new DefaultListModel<>();
    private JList<Offer> offerList = new JList<>(offerListModel);
    private DefaultListModel<String> reviewListModel = new DefaultListModel<>();
    private JList<String> reviewList = new JList<>(reviewListModel);
    
    private JTextField fieldPriceOffer = new JTextField();
    private JSpinner spinnerQty;
    private JTextArea areaDescription = new JTextArea();
    private JLabel lblVendido;

    public ShowSaleGUI(Sale sale, String currentUserEmail, QuerySalesGUI parent) {
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(750, 600));
        this.setTitle("Detalle de Producto: " + sale.getTitle());
        this.setLocationRelativeTo(null);

        BLFacade facade = MainGUI.getBusinessLogic();
        Seller srv = sale.getSeller();

        // 1. INFO VENDEDOR
        String infoVendedor = String.format("%s | Media: %.1f ⭐ (%d votos)", 
                srv.getName(), srv.getAverageRating(), srv.getNumVotes());
        JLabel lblSellerInfo = new JLabel(infoVendedor);
        lblSellerInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblSellerInfo.setBounds(20, 15, 450, 20);
        getContentPane().add(lblSellerInfo);

        // 2. TÍTULO Y PRECIO
        JLabel lblProductInfo = new JLabel(sale.getTitle() + " - " + sale.getPrice() + "€/ud");
        lblProductInfo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblProductInfo.setBounds(20, 45, 400, 25);
        getContentPane().add(lblProductInfo);

        // 3. FAVORITOS
        JButton btnFav = new JButton();
        User userActual = facade.getUser(currentUserEmail);
        btnFav.setText(userActual.getFavorites().contains(sale) ? "❤️ Quitar Favorito" : "🤍 Añadir Favorito");
        btnFav.setBounds(480, 45, 160, 25);
        btnFav.addActionListener(e -> {
            if (facade.toggleFavorite(currentUserEmail, sale.getSaleNumber())) {
                User u = facade.getUser(currentUserEmail);
                btnFav.setText(u.getFavorites().contains(sale) ? "❤️ Quitar Favorito" : "🤍 Añadir Favorito");
                if (parent != null) parent.refrescar();
            }
        });
        getContentPane().add(btnFav);

        // 4. DESCRIPCIÓN (Scroll intacto)
        areaDescription.setText(sale.getDescription());
        areaDescription.setEditable(false);
        areaDescription.setLineWrap(true);
        areaDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(areaDescription);
        scrollDesc.setBounds(20, 105, 320, 70);
        getContentPane().add(scrollDesc);

        // 5. STOCK
        JLabel lblStock = new JLabel("Stock: " + sale.getStock() + " unidades");
        lblStock.setForeground(Color.BLUE);
        lblStock.setBounds(20, 180, 200, 20);
        getContentPane().add(lblStock);

        // 6. PUJAS
        JScrollPane scrollOffers = new JScrollPane(offerList);
        scrollOffers.setBounds(20, 230, 320, 110);
        for (Offer o : sale.getOffers()) offerListModel.addElement(o);
        getContentPane().add(scrollOffers);

        // 7. REVIEWS (Derecha intacta)
        JScrollPane scrollRev = new JScrollPane(reviewList);
        scrollRev.setBounds(370, 105, 330, 235);
        if (srv.getReviews() != null) {
            for (Review r : srv.getReviews()) reviewListModel.addElement("⭐ " + r.getScore() + " - " + r.getComment());
        }
        getContentPane().add(scrollRev);

        // 8. ACCIÓN (Oferta con cantidad)
        boolean esDuenio = srv.getEmail().equals(currentUserEmail);
        JButton btnAction = new JButton(esDuenio ? "Aceptar Oferta" : "Hacer Oferta");
        btnAction.setBounds(225, 380, 250, 35);
        
        lblVendido = new JLabel("");
        lblVendido.setHorizontalAlignment(SwingConstants.CENTER);
        lblVendido.setForeground(new Color(0, 128, 0));
        lblVendido.setBounds(50, 350, 600, 20);
        getContentPane().add(lblVendido);

        if (sale.getStatus() == 4 || sale.getStock() <= 0) {
            User c = facade.getUser(sale.getBuyerEmail());
            lblVendido.setText("VENDIDO A: " + (c != null ? c.getName() : "---"));
            btnAction.setEnabled(false);
        } else if (!esDuenio) {
            JLabel lblP = new JLabel("€/ud:"); lblP.setBounds(225, 420, 40, 25); getContentPane().add(lblP);
            fieldPriceOffer.setBounds(265, 420, 60, 25); getContentPane().add(fieldPriceOffer);
            JLabel lblC = new JLabel("Cant:"); lblC.setBounds(340, 420, 40, 25); getContentPane().add(lblC);
            spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, sale.getStock(), 1));
            spinnerQty.setBounds(380, 420, 50, 25); getContentPane().add(spinnerQty);
        }

        btnAction.addActionListener(e -> {
            if (esDuenio) {
                Offer o = offerList.getSelectedValue();
                if (o != null && facade.acceptOffer(o.getBuyerEmail(), sale, o)) {
                    MainGUI.actualizarSaldo();
                    JOptionPane.showMessageDialog(this, "Venta parcial realizada.");
                    if (parent != null) parent.refrescar();
                    dispose();
                }
            } else {
                try {
                    float p = Float.parseFloat(fieldPriceOffer.getText());
                    int q = (int) spinnerQty.getValue();
                    facade.createOffer(sale, currentUserEmail, p, q);
                    offerListModel.addElement(new Offer(p, currentUserEmail, sale, q));
                    fieldPriceOffer.setText("");
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error."); }
            }
        });
        getContentPane().add(btnAction);

        // 9. BOTÓN RESEÑA
        if (currentUserEmail.equals(sale.getBuyerEmail()) && !sale.isRated()) {
            JButton btnRate = new JButton("⭐ Valorar");
            btnRate.setBounds(250, 470, 200, 30);
            btnRate.addActionListener(ev -> { new RateSellerGUI(sale, parent).setVisible(true); dispose(); });
            getContentPane().add(btnRate);
        }

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(20, 510, 100, 30);
        btnCerrar.addActionListener(ev -> dispose());
        getContentPane().add(btnCerrar);
    }
}