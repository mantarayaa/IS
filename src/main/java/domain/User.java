package domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;

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

	public User() {
		super();
	}

	public User(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.balance = 0.0f; 
	}

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public float getBalance() { return balance; }
	public void setBalance(float balance) { this.balance = balance; }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		User other = (User) obj;
		return email != null && email.equals(other.email);
	}
}