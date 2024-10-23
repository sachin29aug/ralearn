package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_table")
public class User extends BaseModel {
    public String email;

    public String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Finder<Long, User> find = new Finder(User.class);

    public static User find(String id) {
        return find.byId(Long.valueOf(id));
    }

    public static User authenticate(String email, String password) {
        return find.query().where().ieq("email", email).eq("password", password).findOne();
    }
}
