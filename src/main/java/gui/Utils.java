package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Utils {
	public static ArrayList<String> getStatus() {
		String lang = Locale.getDefault().getLanguage();
		
		if (lang.equals("en")) 
			return new ArrayList<String>(Arrays.asList("New", "Very Good", "Acceptable", "Very Used", "Sold"));
		
		if (lang.equals("es")) 
			return new ArrayList<String>(Arrays.asList("Nuevo", "Muy Bueno", "Aceptable", "Lo ha dado todo", "Vendido"));
		
		if (lang.equals("eus")) 
			return new ArrayList<String>(Arrays.asList("Berria", "Oso Ona", "Egokia", "Oso zaharra", "Salduta"));
		
		return new ArrayList<String>(Arrays.asList("New", "Very Good", "Acceptable", "Very Used", "Sold"));
	}
	
	public static String getStatus(int t) {
		ArrayList<String> status = getStatus();
		if (t < 0 || t >= status.size()) {
			return "Unknown";
		}
		return status.get(t);
	}
}