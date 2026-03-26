package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import domain.User;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

public class LoginGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private BLFacade facade;
    
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JRadioButton rbSeller;
    private JRadioButton rbBuyer;
    private JLabel lblMessage;

    public LoginGUI(BLFacade appFacadeInterface) {
        this.facade = appFacadeInterface;
        
        setTitle("Iniciar Sesión");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);
        
        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);
        
        rbBuyer = new JRadioButton("Comprador", true); 
        rbSeller = new JRadioButton("Vendedor");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbBuyer);
        bg.add(rbSeller);
        
        panel.add(rbBuyer);
        panel.add(rbSeller);
        
        JButton btnLogin = new JButton("Entrar");
        JButton btnBack = new JButton("Volver");
        
        panel.add(btnBack);
        panel.add(btnLogin);
        
        lblMessage = new JLabel("");
        lblMessage.setForeground(Color.RED);
        panel.add(lblMessage);
        
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String password = new String(txtPassword.getPassword());
                boolean isSeller = rbSeller.isSelected(); 
                
                User user = facade.doLogin(email, password);
                
                if (user != null) {
                    System.out.println("Login correcto: " + user.getEmail() + " como vendedor: " + isSeller);
                    MainGUI.setBussinessLogic(facade);
                    MainGUI main = new MainGUI(user.getEmail(), isSeller);
                    main.setVisible(true);
                    dispose(); 
                } else {
                    lblMessage.setText("Credenciales incorrectas");
                }
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StartWindowGUI start = new StartWindowGUI(facade);
                start.setVisible(true);
                dispose();
            }
        });
        
        setContentPane(panel);
    }
}