package models;

import io.ebean.DB;
import io.ebean.Finder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.springframework.util.CollectionUtils;
import utils.GoogleBookClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Entity
public class Book extends BaseModel {
    private String title;

    private String author;

    private Float rating;

    private Integer ratingCount;

    private String published;

    private String category;

    @Column(length = 500)
    private String grUrl;

    @OneToOne
    @JoinColumn(name = "gl_book_id")
    private GBBook gbBook;

    @OneToOne
    @JoinColumn(name = "ol_book_id")
    private OLBook olBook;

    public static Finder<Long, Book> find = new Finder<>(Book.class);

    // Static methods

    public static Book findByTitle(String title) {
        List<Book> books = find.query().where().ieq("title", title).findList();
        return CollectionUtils.isEmpty(books) ? null : books.get(0);
    }

    public static List<Book> getRandomBooks(List<String> subCategories) {
        List<Book> randomBooks = new ArrayList<>();
        Book randomBook = null;
        try {
            for(String subCategory : subCategories) {
                randomBook = getRandomBookByCategory(null, subCategory);
                randomBooks.add(randomBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return randomBooks;
    }

    public static Book getRandomBookByCategory(String category, String subCategory) {
        Book randomBook;
        if(category != null) {
            randomBook = DB.find(Book.class).where().eq("category", "spirituality").gt("rating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
            //Category categoryObj = Category.findByTitle(category);
            //randomBook = DB.find(Book.class).where().in("category", category).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else if(subCategory != null) {
            randomBook = DB.find(Book.class).where().eq("category", subCategory).gt("rating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else {
            randomBook = DB.find(Book.class).where().gt("rating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        }

        GoogleBookClient.importGoogleBookInfo(randomBook);
        randomBook.refresh();

        return randomBook;
    }

    // Non-static methods

    public String getTitle(int length) {
        if(title != null && title.length() > length) {
            return title.substring(0, length);
        }
        return title;
    }

    public String getDescription(int length) {
        String googleBookDescription = "";
        if(this.gbBook != null) {
            googleBookDescription = this.gbBook.getDescription();
            if (googleBookDescription != null && googleBookDescription.length() > length) {
                return googleBookDescription.substring(0, length);
            }
            return googleBookDescription;
        }
        return googleBookDescription;
    }

    public String getHtmlDescription() {
        return this.gbBook.getDescription().replaceAll("(?<=\\.)\\s+", "</p><p>");
    }

    public String getGrAbsoluteUrl() {
        return "https://www.goodreads.com/" + grUrl;
    }

    public String getAmazonUrl() throws UnsupportedEncodingException {
        return "https://www.amazon.com/s?k=" + URLEncoder.encode(title.toLowerCase(), "UTF-8");
    }

    public String getYoutubeUrl() throws UnsupportedEncodingException {
        return "https://www.youtube.com/results?search_query=" + URLEncoder.encode("Book summary for" + title.toLowerCase() + " by " + author, "UTF-8");
    }

    // Getters Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrUrl() {
        return grUrl;
    }

    public void setGrUrl(String grUrl) {
        this.grUrl = grUrl;
    }

    public GBBook getGbBook() {
        return gbBook;
    }

    public void setGbBook(GBBook gbBook) {
        this.gbBook = gbBook;
    }

    public OLBook getOlBook() {
        return olBook;
    }

    public void setOlBook(OLBook olBook) {
        this.olBook = olBook;
    }
}
