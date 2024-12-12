package models;

import io.ebean.DB;
import io.ebean.Finder;
import jakarta.persistence.*;
import org.springframework.util.CollectionUtils;
import utils.GoogleBookClient;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "author"})})
public class Book extends BaseModel {
    // TODO:
    //  => Include shelved count in BookCategory
    //  => Data Quality
    //  => Data load GR and CPT
    //  => Authenticated and SSL annotations
    //  => CommonUtil.getRequestBodyParam() everywhere
    //  => User find(String id)..should be long

    private String title;

    private String author;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    private Integer ratingCount;

    private String published;

    @Column(length = 500)
    private String grUrl;

    @ManyToOne
    private Category category;

    @OneToOne
    @JoinColumn(name = "gl_book_id")
    private GBBook gbBook;

    @OneToOne
    @JoinColumn(name = "cpt_book_id")
    private CPTBook cptBook;

    @OneToOne
    @JoinColumn(name = "ol_book_id")
    private OLBook olBook;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategory> bookCategories;

    @OneToMany(mappedBy = "book")
    private List<Quote> quotes;

    public static Finder<Long, Book> find = new Finder<>(Book.class);

    // Static methods

    public static Book find(Long id) {
        return find.byId(id);
    }

    public static Book findByTitle(String title) {
        List<Book> books = find.query().where().eq("title", title).findList();
        return CollectionUtils.isEmpty(books) ? null : books.get(0);
    }

    public static Book findByTitleAndAuthor(String title, String author) {
        return find.query().where().eq("title", title).eq("author", author).findOne();
    }

    public static Book getRandomBookByCategory(Long parentCategoryId, Long categoryId) {
        Book randomBook;
        BookCategory bookCategory;
        if(parentCategoryId != null) {
            bookCategory = DB.find(BookCategory.class).where().eq("category.parent.id", parentCategoryId).gt("book.rating", 3.7).gt("book.ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else if(categoryId != null) {
            bookCategory = DB.find(BookCategory.class).where().eq("category.id", categoryId).gt("book.rating", 3.7).gt("book.ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        } else {
            bookCategory = DB.find(BookCategory.class).where().gt("book.rating", 3.7).gt("book.ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
        }

        randomBook = bookCategory.getBook();
        //GoogleBookClient.importGoogleBookInfo(randomBook);
        //randomBook.refresh();

        return randomBook;
    }

    public static List<Book> getRandomBooksCPT(int count) {
        return find.query().where().isNull("cptBook.id").setMaxRows(count).orderBy("RANDOM()").findList();
    }

    public static List<Book> search(String searchTerm) {
        return find.query().where().or().ilike("title", "%" + searchTerm + "%").ilike("author", "%" + searchTerm + "%").endOr().setMaxRows(5).findList();
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

    public List<Quote> getBookQuotes() {
        return this.getQuotes().stream().filter(quote -> quote.getAuthor() == null).collect(Collectors.toList());
    }

    public List<Quote> getAuthorQuotes() {
        return this.getQuotes().stream().filter(quote -> quote.getAuthor() != null).collect(Collectors.toList());
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

    public String getRatingCategoryText() {
        if (rating == null || ratingCount == null) {
            return null;
        }

        if (rating.compareTo(new BigDecimal("5.0")) == 0 && ratingCount > 500) {
            return "Top Rated";
        } else if (rating.compareTo(new BigDecimal("4.0")) >= 0 && ratingCount > 500) {
            return "Highly Rated";
        } else if (rating.compareTo(new BigDecimal("3.0")) >= 0 && ratingCount > 500) {
            return "Well-Liked";
        } else if (rating.compareTo(new BigDecimal("3.0")) >= 0 && ratingCount < 100) {
            return "Hidden Gem";
        } else if (rating.compareTo(new BigDecimal("3.0")) < 0 && ratingCount < 100) {
            return "For the Curious";
        } else {
            return null;
        }
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

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getGrUrl() {
        return grUrl;
    }

    public void setGrUrl(String grUrl) {
        this.grUrl = grUrl;
    }

    public GBBook getGbBook() {
        if(gbBook == null) {
            return GoogleBookClient.importGoogleBookInfo(this);
        }
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

    public List<BookCategory> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(List<BookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }

    public CPTBook getCptBook() {
        return cptBook;
    }

    public void setCptBook(CPTBook cptBook) {
        this.cptBook = cptBook;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
