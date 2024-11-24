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
    public String title;

    public String author;

    public Float averageRating;

    public Integer ratingCount;

    public String publishDate;

    @Column(length = 500)
    public String goodReadsUrl;

    public String category;

    public String subCategory;

    public Long orderIndex;

    @OneToOne
    @JoinColumn(name = "gl_book_id")
    private GBBook gbBook;

    @OneToOne
    @JoinColumn(name = "ol_book_id")
    private OLBook olBook;

    public static Finder<Long, Book> find = new Finder<>(Book.class);

    // Static methods

    public static Book findByOrderIndex(String subCategory, int orderIndex) {
        return find.query().where().eq("subCategory", subCategory).eq("order_index", orderIndex).findOne();
    }

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
                //randomBook.update();
                randomBooks.add(randomBook);
            }
        } catch (Exception e) {
            System.out.printf(randomBook.title == null ? "blank" : randomBook.title);
            System.out.printf(randomBook.author == null ? "blank" : randomBook.author);
            System.out.printf(randomBook.publishDate == null ? "blank" : randomBook.publishDate);
            e.printStackTrace();
        }
        return randomBooks;
    }

    public static Book getRandomBookByCategory(String category, String subCategory) {
        Book randomBook;
        if(category != null) {
            randomBook = DB.find(Book.class).where().eq("category", category).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else if(subCategory != null) {
            randomBook = DB.find(Book.class).where().eq("subCategory", subCategory).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else {
            randomBook = DB.find(Book.class).where().gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        }

        GoogleBookClient.importGoogleBookInfo(randomBook);
        randomBook.refresh();

        return randomBook;
    }

    // Getters and Setters

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

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getGoodReadsUrl() {
        return goodReadsUrl;
    }

    public void setGoodReadsUrl(String goodReadsUrl) {
        this.goodReadsUrl = goodReadsUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCoverImageUrl() {
        GBBook gbBook = this.getGbBook();
        return gbBook != null ? gbBook.getThumbnailUrl() : "";
    }

    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
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

    // Other non-static methods

    public String getTitle(int length) {
        if(title != null && title.length() > length) {
            return title.substring(0, length);
        }
        return title;
    }

    public String getEncodedTitle() throws UnsupportedEncodingException {
        return URLEncoder.encode(title.toLowerCase(), "UTF-8");
    }

    public String getEncodedTitleForSummary() throws UnsupportedEncodingException {
        return URLEncoder.encode("Book summary for" + title.toLowerCase() + " by " + author, "UTF-8");
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

    public String getGoodReadsAbsoluteUrl() {
        return "https://www.goodreads.com/" + goodReadsUrl;
    }

    public String getAmazonUrl() throws UnsupportedEncodingException {
        return "https://www.amazon.com/s?k=" + this.getEncodedTitle();
    }

    public String getYoutubeUrl() throws UnsupportedEncodingException {
        return "https://www.youtube.com/results?search_query=" + this.getEncodedTitleForSummary();
    }

    public String getGoogleBooksUrl() throws UnsupportedEncodingException {
        GBBook gbBook = this.getGbBook();
        return gbBook != null ? gbBook.getPreviewLink() : "";
    }
}
