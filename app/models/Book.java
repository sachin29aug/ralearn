package models;

import io.ebean.DB;
import io.ebean.Finder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import repository.ComputerRepository;
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

    public String isbn;

    public String coverImageUrl;

    public String description;

    public String previewUrl;

    public String authorDescription;

    public Long orderIndex;

    public static Finder<Long, Book> find = new Finder(Book.class);

    // Static methods

    public static Book findByOrderIndex(String subCategory, int orderIndex) {
        return find.query().where().eq("subCategory", subCategory).eq("order_index", orderIndex).findOne();
    }

    public static List<Book> getRandomBooks(List<String> subCategories) {
        List<Book> randomBooks = new ArrayList<>();
        Book randomBook = null;
        try {
            for(String subCategory : subCategories) {
                randomBook = getRandomBookBySubcategory(subCategory);
                //randomBook.update();
                randomBooks.add(randomBook);
            }
        } catch (Exception e) {
            System.out.printf(randomBook.title == null ? "blank" : randomBook.title);
            System.out.printf(randomBook.author == null ? "blank" : randomBook.author);
            System.out.printf(randomBook.publishDate == null ? "blank" : randomBook.publishDate);
            System.out.printf(randomBook.isbn == null ? "blank" : randomBook.isbn);
            System.out.printf(randomBook.coverImageUrl == null ? "blank" : randomBook.coverImageUrl);
            System.out.printf(randomBook.previewUrl == null ? "blank" : randomBook.previewUrl);
            e.printStackTrace();
        }
        return randomBooks;
    }

    public static Book getRandomBookBySubcategory(String subCategory) {
        //int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).findCount();
        //int randomIndex = new Random().nextInt(subCategoryBooksCount);
        //Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
        Book randomBook = DB.find(Book.class).where().eq("subCategory", subCategory).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        Map<String, String> map = GoogleBookClient.getCoverImageUrlAndIsbn(randomBook.title);
        randomBook.setCoverImageUrl(map.get("coverImageUrl"));
        randomBook.setIsbn(map.get("isbn"));
        randomBook.setDescription(map.get("description"));
        randomBook.setPreviewUrl(map.get("previewUrl"));
        randomBook.setAuthorDescription(map.get("authorDescription"));
        randomBook.update();
        //ComputerRepository.update(randomBook.id, randomBook.coverImageUrl, randomBook.isbn, randomBook.description, randomBook.previewUrl, randomBook.authorDescription);
        return randomBook;
    }

    // Non-static methods

    public String coverImageUrl() {
        return this.coverImageUrl;
    }

    public String getShortTitle() {
        if(title.length() > 50) {
            return title.substring(0, 50);
        }
        return title;
    }

    public String getTitle(int length) {
        if(title.length() > length) {
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

    public String getShortDescription() {
        if(description != null && description.length() > 50) {
            return description.substring(0, 50);
        }
        return description;
    }

    public String subCategory() {
        return this.subCategory;
    }

    // Getter and Setters


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
        return "https://www.goodreads.com/" + goodReadsUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getAuthorDescription() {
        return authorDescription;
    }

    public void setAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }

    public Long getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Long orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getAmazonUrl() throws UnsupportedEncodingException {
        return "https://www.amazon.com/s?k=" + this.getEncodedTitle();
    }

    public String getGoogleBooksUrl() {
        return this.previewUrl;
    }

    public String getYoutubeUrl() throws UnsupportedEncodingException {
        return "https://www.youtube.com/results?search_query=" + this.getEncodedTitleForSummary();
    }

    public String getHtmlDescription() {
        return this.description.replaceAll("(?<=\\.)\\s+", "</p><p>");
    }
}
