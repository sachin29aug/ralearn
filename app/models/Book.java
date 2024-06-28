package models;

import io.ebean.Finder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Book extends BaseModel {
    public String title;

    public String author;

    public String averageRating;

    public String ratingCount;

    public String publishDate;

    @Column(length = 500)
    public String goodReadsUrl;

    public String category;

    public String subCategory;

    public String isbn;

    public String coverImageUrl;

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

    public Book(String title, String author, String averageRating, String ratingCount, String publishDate, String goodReadsUrl, String category, String subCategory, String isbn, String coverImageUrl, Long orderIndex) {
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

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
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

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }
}
