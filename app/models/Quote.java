package models;

import io.ebean.Finder;
import jakarta.persistence.*;

@Entity
public class Quote extends BaseModel {
    public enum Source {
        KAGGLE,
        GR;
    }

    @Lob
    private String text;

    private String authorName;

    @Lob
    private String tags;

    @ManyToOne
    private Author author;

    @ManyToOne
    private Book book;

    private Integer likesCount;

    @Enumerated(EnumType.STRING)
    private Source source;

    private static Finder<Long, Quote> find = new Finder<>(Quote.class);

    // Static methods

    public static Quote getRandomQuote() {
        Quote quote = find.query().where().isNull("book.id").setMaxRows(1).orderBy("RANDOM()").findOne();
        return quote;
    }

    // Getters and Setters

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}