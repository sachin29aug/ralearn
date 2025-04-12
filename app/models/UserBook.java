package models;

import io.ebean.Finder;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import utils.CommonUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class UserBook extends BaseModel {
    public enum AssignmentType {
        SYSTEM,
        MANUAL;
    }

    @ManyToOne
    public User user;

    @ManyToOne
    public Book book;

    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType;

    public Date assigned;

    public Date favorited;

    public Date accessed;

    public UserBook(User user, Book book, Category category, AssignmentType assignmentType) {
        this.user = user;
        this.book = book;
        this.category = category;
        this.assignmentType  = assignmentType;
        if(assignmentType != null) {
            this.assigned = new Date();
        }
    }

    public static Finder<Long, UserBook> find = new Finder<>(UserBook.class);

    public static List<UserBook> findTodayUserBooks(Long userId) {
        Date today = CommonUtil.removeTimeStamp(new Date());
        Date tomorrow = CommonUtil.incrementDateByDays(today, 1);
        return find.query().where().ge("assigned", today).lt("assigned", tomorrow).eq("user.id", userId).orderBy("id desc").findList();
    }

    public static List<UserBook> findUserBooksByCategory(Long userId, Long categoryId) {
        return find.query().where().eq("user.id", userId).eq("category.id", categoryId).orderBy("assigned desc").findList();
    }

    public static List<UserBook> findPastUserBooksByCategory(Long userId, Long categoryId) {
        return find.query().where().eq("user.id", userId).eq("category.id", categoryId).lt("assigned", CommonUtil.removeTimeStamp(new Date())).orderBy("assigned desc").findList();
    }

    public static UserBook findByUserAndBookId(Long userId, Long bookId) {
        return find.query().where().eq("user.id", userId).eq("book.id", bookId).findOne();
    }

    public static List<UserBook> findFavoriteUserBooks(Long userId) {
        return find.query().where().eq("user.id", userId).isNotNull("favorited").orderBy("id desc").findList();
    }

    public static List<UserBook> findRecentlyAccessedBooks(Long userId) {
        return find.query().where().eq("user.id", userId).isNotNull("accessed").orderBy("accessed desc").findList();
    }

    public static Date findMaxAssignedDate(Long userId) {
        return find.query().where().eq("user.id", userId).orderBy("assigned desc").setMaxRows(1).findOne().assigned;
    }

    public static void generateRandomUserBooks(User user, boolean today, boolean past) {
       /** List<Category> categories = new ArrayList<>();
        for(UserCategory userCategory : user.userCategories) {
            categories.add(userCategory.getCategory());
        }*/

        // TODO: use streams across.. after this is tested that this is working.. i guess we can use user.getUserCategories() list directly..
        List<Category> categories = user.getUserCategories().stream().map(UserCategory::getCategory).collect(Collectors.toList());
        if(today) {
            for(Category category : categories) {
                Book randomBook = Book.getRandomBookByCategory(null, category.getId());
                UserBook userBook = new UserBook(user, randomBook, category, AssignmentType.SYSTEM);
                userBook.save();
            }
        }

        if(past) {
            for (int i = 5; i >= 1; i--) {
                for(Category category : categories) {
                    Book randomBook = Book.getRandomBookByCategory(null, category.getId());
                    UserBook userBook = new UserBook(user, randomBook, category, AssignmentType.SYSTEM);
                    userBook.assigned = CommonUtil.incrementDateByDays(new Date(), -i);
                    userBook.save();
                }
            }
        }
    }

    // Getter Setters

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public Date getAssigned() {
        return assigned;
    }

    public void setAssigned(Date assigned) {
        this.assigned = assigned;
    }

    public Date getFavorited() {
        return favorited;
    }

    public void setFavorited(Date favorited) {
        this.favorited = favorited;
    }

    public Date getAccessed() {
        return accessed;
    }

    public void setAccessed(Date accessed) {
        this.accessed = accessed;
    }
}
