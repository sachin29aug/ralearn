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
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String passwordResetToken;

    @OneToMany(mappedBy = "user")
    public List<UserCategory> userCategories;

    @OneToMany(mappedBy = "user")
    @OrderBy("id")
    public List<UserBook> userBooks;

    public static Finder<Long, User> find = new Finder<>(User.class);

    public static User find(String id) {
        return find.byId(Long.valueOf(id));
    }

    public static User findByEmail(String email) {
        return find.query().where().ieq("email", email).findOne();
    }

    public static User findByPasswordResetToken(String token) {
        return find.query().where().eq("passwordResetToken", token).findOne();
    }

    public static User authenticate(String email, String password) {
        return find.query().where().ieq("email", email).eq("password", password).findOne();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserCategory> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(List<UserCategory> userCategories) {
        this.userCategories = userCategories;
    }

    public List<UserBook> getUserBooks() {
        return userBooks;
    }

    public void setUserBooks(List<UserBook> userBooks) {
        this.userBooks = userBooks;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }
}
