package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BookCategory extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    public Book book;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    public Category category;

    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public static Finder<Long, BookCategory> find = new Finder<>(BookCategory.class);

    public Book getBook() {
        return book;
    }

    public Category getCategory() {
        return category;
    }
}
