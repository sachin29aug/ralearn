package models;

import io.ebean.Finder;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"gr_url"})})
public class Author extends BaseModel {
    private String name;

    @Lob
    private String bio; // Source: CPT

    private String grUrl;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    public static Finder<Long, Author> find = new Finder<>(Author.class);

    public static Author find(Long id) {
        return find.byId(id);
    }

    public static Author findByGrUrl(String grUrl) {
        return find.query().where().eq("grUrl", grUrl).findOne();
    }

    // Getters Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGrUrl() {
        return grUrl;
    }

    public void setGrUrl(String grUrl) {
        this.grUrl = grUrl;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
