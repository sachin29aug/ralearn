package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import utils.DateUtil;

import java.util.Date;
import java.util.List;

@Entity
public class UserBook extends BaseModel {
    @ManyToOne
    public User user;

    @ManyToOne
    public Book book;

    public Date assigned;

    public UserBook(User user, Book book) {
        this.user = user;
        this.book = book;
        assigned = DateUtil.removeTimeStamp(new Date());
    }

    public static Finder<Long, UserBook> find = new Finder(UserBook.class);

    public static List<UserBook> findTodayUserBooks(Long userId) {
        return find.query().where().eq("assigned", DateUtil.removeTimeStamp(new Date())).eq("user.id", userId).orderBy("assigned desc").findList();
    }

    public static List<UserBook> findPastUserBooksBySubCategory(String subCategory, Long userId) {
        return find.query().where().eq("book.subCategory", subCategory).lt("assigned", DateUtil.removeTimeStamp(new Date())).eq("user.id", userId).orderBy("assigned desc").findList();
    }
}
