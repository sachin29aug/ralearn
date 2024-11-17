package models;

import io.ebean.Finder;
import jakarta.persistence.Column;
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
}