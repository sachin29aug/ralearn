package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class UserCategory extends BaseModel {
    @ManyToOne
    public User user;

    @ManyToOne
    public Category category;

    public UserCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }

    public static Finder<Long, UserCategory> find = new Finder(UserCategory.class);
}
