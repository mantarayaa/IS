package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import domain.Sale;
import businessLogic.BLFacade;

public class RateSellerGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea textArea;
    private JComboBox<Integer> comboBox;

    public RateSellerGUI(Sale sale, QuerySalesGUI parent) {
        setTitle("Valorar Vendedor: " + sale.getSeller().getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 350, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10));

        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.add(new JLabel("Puntuación para la venta de: " + sale.getTitle()));
        
        comboBox = new JComboBox<>(new Integer[] {1, 2, 3, 4, 5});
        panelNorth.add(comboBox);
        contentPane.add(panelNorth, BorderLayout.NORTH);

        textArea = new JTextArea("Escribe un comentario...");
        contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton btnEnviar = new JButton("Enviar Valoración");
        btnEnviar.addActionListener(e -> {
            BLFacade facade = MainGUI.getBusinessLogic();
            int score = (int) comboBox.getSelectedItem();
            boolean ok = facade.rateSeller(sale.getSaleNumber(), score, textArea.getText());
            
            if (ok) {
                JOptionPane.showMessageDialog(null, "Valoración enviada con éxito.");
                if (parent != null) parent.refrescar();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Error al enviar la valoración.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPane.add(btnEnviar, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }
}