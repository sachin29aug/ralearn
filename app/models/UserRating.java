package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class UserRating extends BaseModel {
    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    private Integer rating;

    @Lob
    private String text;

    public static Finder<Long, UserRating> find = new Finder<>(UserRating.class);

    public static UserRating findByUserAndBookId(Long userId, Long bookId) {
        return find.query().where().eq("user.id", userId).eq("book.id", bookId).findOne();
    }

    // Getters Setters Constructors

    public UserRating(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
