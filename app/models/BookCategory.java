package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class BookCategory extends BaseModel {
    @ManyToOne
    public Book book;

    @ManyToOne
    public Category category;

    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

    public static Finder<Long, BookCategory> find = new Finder<>(BookCategory.class);
}
