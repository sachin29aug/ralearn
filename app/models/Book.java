package models;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Finder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import play.db.NamedDatabase;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Entity
public class Book extends BaseModel {
    public String title;

    public String author;

    public Float averageRating;

    public Integer ratingCount;

    public String publishDate;

    @Column(length = 500)
    public String goodReadsUrl;

    public String category;

    public String subCategory;

    public String isbn;

    public String coverImageUrl;

    public String description;

    public String previewUrl;

    public String authorDescription;

    public Long orderIndex;

    public static Finder<Long, Book> find = new Finder(Book.class);

    public static Book findByOrderIndex(String subCategory, int orderIndex) {
        return find.query().where().eq("subCategory", subCategory).eq("order_index", orderIndex).findOne();
    }

    public static List<Book> findBySubCategory(String subCategory) {
        return find.query().where().eq("subCategory", subCategory).findList();
    }

    public String getShortTitle() {
        if(title.length() > 50) {
            return title.substring(0, 50);
        }
        return title;
    }

    public String getEncodedTitle() throws UnsupportedEncodingException {
        return URLEncoder.encode(title.toLowerCase(), "UTF-8");
    }

    public String getEncodedTitleForSummary() throws UnsupportedEncodingException {
        return URLEncoder.encode("Book summary for" + title.toLowerCase() + " by " + author, "UTF-8");
    }

    public Book(String title, String author, Float averageRating, Integer ratingCount, String publishDate, String goodReadsUrl, String category, String subCategory, String isbn, String coverImageUrl, Long orderIndex) {
        this.title = title;
        this.author = author;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.publishDate = publishDate;
        this.goodReadsUrl = goodReadsUrl;
        this.category = category;
        this.subCategory = subCategory;
        this.isbn = isbn;
        this.coverImageUrl = coverImageUrl;
        this.orderIndex = orderIndex;
    }

    public void update(String coverImageUrl, String isbn, String description, String previewUrl, String authorDescription) {
        this.coverImageUrl = coverImageUrl;
        this.isbn = isbn;
        this.description = description;
        this.previewUrl = previewUrl;
        this.authorDescription = authorDescription;
        update();
    }
}
