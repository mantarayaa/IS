package gui;

import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import businessLogic.BLFacade;
import configuration.UtilDate;

public class CreateSaleGUI extends JFrame {
	
    File targetFile;
    BufferedImage targetImg;
    String encodedfile = null;

    public JPanel panel_1;
    private static final int baseSize = 128;
	private static final String basePath="src/main/resources/images/";

	private static final long serialVersionUID = 1L;

	private String sellerMail;
	private JTextField fieldTitle=new JTextField();
	private JTextField fieldDescription=new JTextField();
	private JTextField jTextFieldPrice = new JTextField();
	private JTextField jTextFieldStock = new JTextField(); // NUEVO
	
	private JLabel jLabelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"));
	private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description")); 
	private JLabel jLabelProductStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
	private JLabel jLabelPrice = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"));
	private JLabel jLabelStock = new JLabel("Stock:"); // NUEVO

	private JCalendar jCalendar = new JCalendar();
	private Calendar calendarAct = null;
	private Calendar calendarAnt = null;

	private JScrollPane scrollPaneEvents = new JScrollPane();
	
	JComboBox<String> jComboBoxStatus = new JComboBox<String>();
	DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
	List<String> status;

	private JButton jButtonCreate = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.CreateProduct"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JLabel jLabelMsg = new JLabel();
	private JLabel jLabelError = new JLabel();
	private JFrame thisFrame;
	private final JButton btnNewButton_2 = new JButton("grabar Imagen"); 
	
	private JCheckBox chckbxSinImagen;
	private JButton btnNewButton; 

	public CreateSaleGUI(String mail) {

		thisFrame=this;
		this.sellerMail=mail;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(604, 420)); // Aumentado ligeramente el alto para que no corte nada
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.CreateProduct"));

		jLabelTitle.setBounds(new Rectangle(6, 24, 92, 20));
		
		jLabelPrice.setBounds(new Rectangle(6, 141, 101, 20));
		jTextFieldPrice.setBounds(new Rectangle(97, 141, 60, 20));
		
		// --- NUEVO: Posicionamiento del Stock sin mover lo demás ---
		jLabelStock.setBounds(new Rectangle(6, 165, 80, 20));
		getContentPane().add(jLabelStock);
		jTextFieldStock.setBounds(new Rectangle(97, 165, 60, 20));
		jTextFieldStock.setText("1");
		getContentPane().add(jTextFieldStock);

		scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));
		jButtonCreate.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		jButtonCreate.setBounds(new Rectangle(100, 250, 216, 41)); // Bajado un poco

		jButtonCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jLabelMsg.setText("");
				String error=check_fields_Errors();
				if (error!=null) 
					jLabelMsg.setText(error);
				else
					try {
						BLFacade facade = MainGUI.getBusinessLogic();
						float price = Float.parseFloat(jTextFieldPrice.getText());
						int stock = Integer.parseInt(jTextFieldStock.getText()); // NUEVO
						
						String s=(String)jComboBoxStatus.getSelectedItem();
						int numStatus=status.indexOf(s);
						
						File imagenAEnviar = targetFile;
						if (chckbxSinImagen.isSelected()) {
							imagenAEnviar = null; 
						}
						
						boolean checkSinImagen = chckbxSinImagen.isSelected();
						// LLAMADA ACTUALIZADA CON STOCK
						facade.createSale(fieldTitle.getText(), fieldDescription.getText(), numStatus, price, UtilDate.trim(jCalendar.getDate()), sellerMail, imagenAEnviar, checkSinImagen, stock);
						jLabelMsg.setText(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ProductCreated"));
					
					} catch (Exception e1) {
						jLabelMsg.setText(e1.getMessage());
					}
			}
		});
		
		jButtonClose.setBounds(new Rectangle(328, 256, 101, 30)); // Bajado un poco
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);			}
		});

		jLabelMsg.setBounds(new Rectangle(26, 310, 377, 20));
		jLabelMsg.setForeground(Color.red);

		jLabelError.setBounds(new Rectangle(16, 310, 384, 20));
		jLabelError.setForeground(Color.red);
		
        status=Utils.getStatus();
		for(String s:status) statusOptions.addElement(s);

		this.getContentPane().add(jLabelMsg, null);
		this.getContentPane().add(jLabelError, null);
		this.getContentPane().add(jButtonClose, null);
		this.getContentPane().add(jButtonCreate, null);
		this.getContentPane().add(jLabelTitle, null);
		this.getContentPane().add(jLabelPrice, null);
		this.getContentPane().add(jTextFieldPrice, null);
		
		jLabelProductStatus.setBounds(6, 195, 140, 25); // Bajado un poco para dejar sitio al stock
		getContentPane().add(jLabelProductStatus);
		
		jLabelDescription.setBounds(6, 56, 109, 16);
		getContentPane().add(jLabelDescription);
		
		fieldTitle.setBounds(98, 21, 250, 26);
		getContentPane().add(fieldTitle);
		
		fieldDescription.setBounds(98, 56, 250, 73);
		getContentPane().add(fieldDescription);
		
		jComboBoxStatus.setModel(statusOptions);
		jComboBoxStatus.setBounds(90, 193, 114, 27); // Ajustado
		getContentPane().add(jComboBoxStatus);
		
		btnNewButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.LoadPicture"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF", "jpg", "gif");
				fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(null);  
                if (result == JFileChooser.APPROVE_OPTION) {
                    targetFile = fileChooser.getSelectedFile();
                    panel_1.removeAll();
                    panel_1.repaint();
                    try {
                        targetImg = rescale(ImageIO.read(targetFile));
                    } catch (IOException ex) {}
                    panel_1.setLayout(new BorderLayout(0, 0));
                    panel_1.add(new JLabel(new ImageIcon(targetImg))); 
                    setVisible(true);
                }
			}
		});
		btnNewButton.setBounds(186, 138, 162, 29);
		getContentPane().add(btnNewButton);
		
		chckbxSinImagen = new JCheckBox("Crear sin imagen");
		chckbxSinImagen.setBounds(198, 173, 150, 23); 
		chckbxSinImagen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxSinImagen.isSelected()) {
					btnNewButton.setEnabled(false);
					targetFile = null; 
					panel_1.removeAll(); 
					panel_1.repaint();
				} else {
					btnNewButton.setEnabled(true);
				}
			}
		});
		getContentPane().add(chckbxSinImagen);
		
		panel_1 = new JPanel();
		panel_1.setBounds(461, 230, 124, 86); // Ajustado
		getContentPane().add(panel_1);
		
		jCalendar.setBounds(new Rectangle(360, 50, 225, 150));
		this.getContentPane().add(jCalendar, null);
		
		JLabel jLabelPublicationDate = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"));
		jLabelPublicationDate.setBounds(360, 26, 197, 20);
		getContentPane().add(jLabelPublicationDate);
	}   

	public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }

	private String check_fields_Errors() {
		try {
			if ((fieldTitle.getText().length()==0) || (fieldDescription.getText().length()==0)  || (jTextFieldPrice.getText().length()==0) || (jTextFieldStock.getText().length()==0))
				return ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ErrorQuery");
			
			float price = Float.parseFloat(jTextFieldPrice.getText());
			int stock = Integer.parseInt(jTextFieldStock.getText());
			
			if (price <= 0) return ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PriceMustBeGreaterThan0");
			if (stock <= 0) return "El stock debe ser mayor que 0";
			
			return null;
		} catch (Exception e1) {
			return ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.ErrorNumber");		
		}
	}
}