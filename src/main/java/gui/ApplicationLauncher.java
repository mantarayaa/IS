package gui;

import java.net.URL;
import java.util.Locale;
import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML; // CRUCIAL: Este import soluciona tu error
import dataAccess.DataAccess;
import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;

public class ApplicationLauncher { 
	
	public static void main(String[] args) {

		ConfigXML c = ConfigXML.getInstance();		
		Locale.setDefault(new Locale(c.getLocale()));

		try {
			BLFacade appFacadeInterface;
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
			if (c.isBusinessLogicLocal()) {
				DataAccess da = new DataAccess();
				appFacadeInterface = new BLFacadeImplementation(da);
			}
			else { 
				 String serviceName = "http://"+c.getBusinessLogicNode() +":"+ c.getBusinessLogicPort()+"/ws/"+c.getBusinessLogicName()+"?wsdl";   
				 URL url = new URL(serviceName);
				 QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
				 Service service = Service.create(url, qname);
				 appFacadeInterface = service.getPort(BLFacade.class);
			} 
			
			StartWindowGUI startWindow = new StartWindowGUI(appFacadeInterface);
			startWindow.setVisible(true);
			
		} catch (Exception e) {
			System.out.println("Error in ApplicationLauncher: " + e.toString());
			e.printStackTrace();
		}
	}
}