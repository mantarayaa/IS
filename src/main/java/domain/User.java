package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlID
	@Id 
	private String email;
	private String name; 
	private String password;
	private float balance; 

	@XmlIDREF
	@ManyToMany(fetch=FetchType.EAGER)
	private List<Sale> favorites = new ArrayList<Sale>();

	public User() { super(); }
	public User(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.balance = 0.0f; 
	}

	public String getEmail() { return email; }
	public String getName() { return name; }
	public float getBalance() { return balance; }
	public void setBalance(float balance) { this.balance = balance; }
	public String getPassword() { return password; }
	
	public List<Sale> getFavorites() { return favorites; }
	
	// Añade esto al final de User.java si no lo tienes
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    User other = (User) obj;
	    return email != null && email.equals(other.email);
	}

	@Override
	public int hashCode() {
	    return email != null ? email.hashCode() : 0;
	}
}