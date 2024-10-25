package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "user_table")
public class User extends BaseModel {
    public String email;

    public String password;

    @OneToMany(mappedBy = "user")
    public List<UserCategory> userCategories;

    @OneToMany(mappedBy = "user")
    @OrderBy("id")
    public List<UserBook> userBooks;

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
