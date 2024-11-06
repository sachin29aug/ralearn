package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class UserFeedback extends BaseModel {
    @ManyToOne
    public User user;

    @Lob
    public String feedback;

    public UserFeedback(User user, String feedback) {
        this.user = user;
        this.feedback = feedback;
    }

    public static Finder<Long, UserFeedback> find = new Finder(UserFeedback.class);
}
