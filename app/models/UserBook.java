package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import utils.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class UserBook extends BaseModel {
    @ManyToOne
    public User user;

    @ManyToOne
    public Book book;

    public Date assigned;

    public Date lastAccessed;

    public Boolean favorite;

    public UserBook(User user, Book book) {
        this.user = user;
        this.book = book;
        assigned = DateUtil.removeTimeStamp(new Date());
    }

    public static Finder<Long, UserBook> find = new Finder(UserBook.class);

    public static List<UserBook> findTodayUserBooks(Long userId) {
        return find.query().where().eq("assigned", DateUtil.removeTimeStamp(new Date())).eq("user.id", userId).orderBy("id desc").findList();
    }

    public static List<UserBook> findPastUserBooksBySubCategory(String subCategory, Long userId) {
        return find.query().where().eq("book.subCategory", subCategory).lt("assigned", DateUtil.removeTimeStamp(new Date())).eq("user.id", userId).orderBy("assigned desc").findList();
    }

    public static UserBook findByUserAndBookId(Long userId, Long bookId) {
        return find.query().where().eq("user.id", userId).eq("book.id", bookId).findOne();
    }

    public static List<UserBook> findFavoriteUserBooks(Long userId) {
        return find.query().where().eq("user.id", userId).eq("favorite", true).orderBy("id desc").findList();
    }

    public static List<UserBook> findRecentlyAccessedBooks(Long userId) {
        return find.query().where().eq("user.id", userId).isNotNull("lastAccessed").orderBy("lastAccessed desc").findList();
    }

    public static Date findMaxAssignedDate(Long userId) {
        return find.query().where().eq("user.id", userId).orderBy("assigned desc").setMaxRows(1).findOne().assigned;
    }

    public static void generateUserBooks(User user, boolean today, boolean past) {
        List<String> subCategoryTitles = new ArrayList<>();
        for(UserCategory userCategory : user.userCategories) {
            Category category = Category.find("" + userCategory.category.id);
            subCategoryTitles.add(category.title);
        }

        List<UserBook> userBooks = new ArrayList<>();
        if(today) {
            List<Book> books = Book.getRandomBooks(subCategoryTitles);
            for (Book book : books) {
                UserBook userBook = new UserBook(user, book);
                userBook.save();
                userBooks.add(userBook);
            }
        }

        if(past) {
            for (int i = 5; i >= 1; i--) {
                List<Book> books = Book.getRandomBooks(subCategoryTitles);
                for (Book book : books) {
                    UserBook userBook = new UserBook(user, book);
                    userBook.assigned = DateUtil.incrementDateByDays(new Date(), -i);
                    userBook.save();
                }
            }
        }
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }
}
