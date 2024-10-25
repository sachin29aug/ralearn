package models;

import io.ebean.DB;
import io.ebean.Finder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import repository.ComputerRepository;
import utils.GoogleBookClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public static List<Book> findBySubCategory(String subCategory) {
        return find.query().where().eq("subCategory", subCategory).findList();
    }

    public static List<Book> getRandomBooks(List<String> subCategories) {
        List<Book> randomBooks = new ArrayList<>();
        for(String subCategory : subCategories) {
            int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).findCount();
            int randomIndex = new Random().nextInt(subCategoryBooksCount);
            Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
            //Book randomBook = DB.find(Book.class).where().eq("subCategory", subCategory).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
            Map<String, String> map = GoogleBookClient.getCoverImageUrlAndIsbn(randomBook.title);
            randomBook.coverImageUrl = map.get("coverImageUrl");
            randomBook.isbn = map.get("isbn");
            randomBook.description = map.get("description");
            randomBook.previewUrl = map.get("previewUrl");
            randomBook.authorDescription = map.get("authorDescription");
            ComputerRepository.update(randomBook.id, randomBook.coverImageUrl, randomBook.isbn, randomBook.description, randomBook.previewUrl, randomBook.authorDescription);
            //randomBook.update();
            randomBooks.add(randomBook);
        }
        return randomBooks;
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
