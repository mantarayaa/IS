package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

public class StartWindowGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private BLFacade facade;
    
    private JButton btnLogin;
    private JButton btnRegister;

    public StartWindowGUI(BLFacade appFacadeInterface) {
        this.facade = appFacadeInterface;
        
        setTitle("Bienvenido a Market");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("Seleccione una opción", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        btnLogin = new JButton("Iniciar Sesión");
        btnRegister = new JButton("Registrarse");
        
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LoginGUI login = new LoginGUI(facade);
                login.setVisible(true);
                dispose(); 
            }
        });
        
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegisterGUI register = new RegisterGUI(facade);
                register.setVisible(true);
                dispose();
            }
        });

        
        panel.add(lblTitulo);
        panel.add(btnLogin);
        panel.add(btnRegister);
        
        setContentPane(panel);
    }
}