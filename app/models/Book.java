package models;

import jakarta.persistence.Entity;

@Entity
public class Book extends BaseModel {
    public String title;
    public String author;
    public String avgRating;
    public String ratingsCount;
    public String publishDate;
    public String goodReadsUrl;
    public String category;
    public String subCategory;

    public Book() {

    }

    public Book(String title, String author, String avgRating, String ratingsCount, String publishDate, String goodReadsUrl, String category, String subCategory) {
        this.title = title;
        this.author = author;
        this.avgRating = avgRating;
        this.ratingsCount = ratingsCount;
        this.publishDate = publishDate;
        this.goodReadsUrl = goodReadsUrl;
        this.category = category;
        this.subCategory = subCategory;
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

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(String ratingsCount) {
        this.ratingsCount = ratingsCount;
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
}
