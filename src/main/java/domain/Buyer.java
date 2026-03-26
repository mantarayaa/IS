package domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Buyer extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	public Buyer() {
		super();
	}

	public Buyer(String email, String name, String password) {
		super(email, name, password);
	}
}