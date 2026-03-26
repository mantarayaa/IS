package gui;

import javax.swing.*;
import businessLogic.BLFacade;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

public class RegisterGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private BLFacade facade;
    
    private JTextField txtEmail;
    private JTextField txtName;
    private JPasswordField txtPassword;
    private JLabel lblMessage;

    public RegisterGUI(BLFacade appFacadeInterface) {
        this.facade = appFacadeInterface;
        
        setTitle("Registro de Usuario");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);
        
        panel.add(new JLabel("Nombre:"));
        txtName = new JTextField();
        panel.add(txtName);
        
        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);
        
        JButton btnRegister = new JButton("Registrar");
        JButton btnBack = new JButton("Volver");
        
        panel.add(btnBack);
        panel.add(btnRegister);
        
        lblMessage = new JLabel("");
        lblMessage.setForeground(Color.RED);
        panel.add(lblMessage);
        
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText();
                String name = txtName.getText();
                String password = new String(txtPassword.getPassword());
                
                if(email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    lblMessage.setText("Rellena todos los campos");
                    return;
                }
                
                boolean ok = facade.registerUser(email, name, password, true);
                
                if (ok) {
                    lblMessage.setForeground(Color.GREEN);
                    lblMessage.setText("Registro exitoso. Vuelve.");
                } else {
                    lblMessage.setForeground(Color.RED);
                    lblMessage.setText("El email ya existe");
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