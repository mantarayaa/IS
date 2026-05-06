package domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Review implements Serializable {
    @Id 
    @GeneratedValue
    private Integer id;
    private int score;
    private String comment;
    private String authorEmail;

    public Review() { super(); }
    public Review(int score, String comment, String authorEmail) {
        this.score = score;
        this.comment = comment;
        this.authorEmail = authorEmail;
    }

    public int getScore() { return score; }
    public String getComment() { return comment; }
    public String getAuthorEmail() { return authorEmail; }
}