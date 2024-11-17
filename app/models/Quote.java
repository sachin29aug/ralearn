package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class Quote extends BaseModel {
    @Lob
    public String text;

    public String author;

    @Lob
    public String tags;

    public static Finder<Long, Quote> find = new Finder<>(Quote.class);

    // Static methods

    public static Quote getRandomQuote() {
        Quote quote = find.query().setMaxRows(1).orderBy("RANDOM()").findOne();
        return quote;
    }

    // Getters and Setters

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}