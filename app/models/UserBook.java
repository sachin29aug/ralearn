package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.List;

@Entity
public class UserBook extends BaseModel {
    @ManyToOne
    public User user;

    @ManyToOne
    public Book book;

    public UserBook(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public static Finder<Long, UserBook> find = new Finder(UserBook.class);
}
