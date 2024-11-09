package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.*;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ComputerRepository;
import utils.DateUtil;
import utils.GoogleBookClient;
import utils.SessionUtil;

import java.time.LocalDate;
import java.util.*;

public class My extends Controller {
    /*@Inject
    private ComputerRepository computerRepository;*/

    /*@Inject
    public My(ComputerRepository computerRepository) {
        this.computerRepository = computerRepository;
    }*/

    public Result home(Long categoryId) {
        String category = "";
        if(categoryId == 1) {
            category = "Personal Development";
        } else if (categoryId == 2) {
            category = "Mind & Spirit";
        } else if (categoryId == 3) {
            category = "Business & Economics";
        } else if (categoryId == 4) {
            category = "Family & Lifestyle";
        } else if (categoryId == 5) {
            category = "Science & Environment";
        } else if (categoryId == 6) {
            category = "Arts & Humanities";
        } else {
            category = "Personal Development";
        }

        List<Book> randomBooks = new ArrayList<>();
        Map<String, List<String>> categoriesMap = Category.getCategoriesMap();
        List<String> subCategories = categoriesMap.get(category);
        for(String subCategory : subCategories) {
            //int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).gt("average_rating", 3.6).gt("rating_count", 1000).findCount();
            //int randomIndex = new Random().nextInt(subCategoryBooksCount);
            //Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
            Book randomBook = DB.find(Book.class).where().eq("subCategory", subCategory).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
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

        return ok(views.html.home.render(category, randomBooks));
    }

    public Result home1(Http.Request request) {
        User user = SessionUtil.getUser(request);
        List<UserBook> userBooks = user.userBooks;
        if(CollectionUtils.isEmpty(userBooks)) {
            UserBook.generateUserBooks(user, true, true);
        }

        return ok(views.html.my.home1.render(user));
    }

    public Result shufflePost(Http.Request request, Long userBookId) {
        Transaction txn = DB.beginTransaction();
        User user = SessionUtil.getUser(request);
        UserBook userBook = UserBook.find.byId(userBookId);
        userBook.setBook(Book.getRandomBookByCategory(null, userBook.book.subCategory())); // When is use setBook() method only then the below update works
        userBook.update();
        txn.commit();

        return ok(views.html.my.home1.render(user));
    }

    public Result favoritePost(Http.Request request, Long bookId) {
        User user = SessionUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, bookId);
        if(BooleanUtils.isNotTrue(userBook.favorite)) {
            userBook.setFavorite(true);
        } else {
            userBook.setFavorite(false);
        }
        userBook.update();

        return ok(views.html.my.home1.render(user));
    }

    public Result list(Http.Request request, String listName) {
        List<UserBook> userBooks;
        User user = SessionUtil.getUser(request);
        if("favorites".equals(listName)) {
            userBooks = UserBook.findFavoriteUserBooks(user.id);
        } else if("recent".equals(listName)) {
            listName = "Recently Viewed";
            userBooks = UserBook.findRecentlyAccessedBooks(user.id);
        } else {
            Category subcategory = Category.findByTitle(listName);
            userBooks = UserBook.findPastUserBooksBySubCategory(subcategory.title, user.id);
        }
        return ok(views.html.my.list.render(listName, userBooks));
    }

    public Result feedback() {
        return ok(views.html.my.feedback.render());
    }

    public Result feedbackPost(Http.Request request) {
        String feedbackText = request.body().asFormUrlEncoded().get("feedbackText")[0];
        UserFeedback userFeedback = new UserFeedback(SessionUtil.getUser(request), feedbackText);
        userFeedback.save();
        return ok(views.html.my.feedback.render());
    }

    public Result discover(Http.Request request) {
        User user = SessionUtil.getUser(request);
        List<Book> randomBooksAcross = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            randomBooksAcross.add(Book.getRandomBookByCategory(null, null));
        }
        return ok(views.html.my.discover.render(user, randomBooksAcross));
    }

    public Result discoverPost(Http.Request request, Long categoryId) {
        User user = SessionUtil.getUser(request);
        Category category = Category.find.byId(categoryId);
        List<UserBook> userBooks = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            if(category.getParent() == null) {
                userBooks.add(new UserBook(user, Book.getRandomBookByCategory(category.getTitle(), null)));
            } else {
                userBooks.add(new UserBook(user, Book.getRandomBookByCategory(null, category.getTitle())));
            }
        }
        return ok(views.html.my.discoverResults.render(category, userBooks));
    }

    public Result profile(Http.Request request) {
        User user = SessionUtil.getUser(request);
        return ok(views.html.my.profile.render(user));
    }
}
