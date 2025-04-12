package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.CommonUtil;

import java.util.*;

public class My extends Controller {

    // Home | Discover | Feedback | Profile related

    public Result home(Http.Request request) {
        User user = CommonUtil.getUser(request);
        List<UserBook> userBooks = user.getUserBooks();
        if(CollectionUtils.isEmpty(userBooks)) {
            UserBook.generateRandomUserBooks(user, true, true);
        }

        return ok(views.html.my.home.render(user));
    }

    public Result discover(Http.Request request) {
        User user = CommonUtil.getUser(request);
        List<Book> randomBooksAcross = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            randomBooksAcross.add(Book.getRandomBookByCategory(null, null));
        }
        return ok(views.html.my.discover.render(user, randomBooksAcross));
    }

    public Result discoverPost(Http.Request request, Long categoryId) {
        User user = CommonUtil.getUser(request);
        Category category = Category.find.byId(categoryId);
        List<UserBook> userBooks = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            // Todo: we should not use model object for this display requirement
            if(category.getParent() == null) {
                userBooks.add(new UserBook(user, Book.getRandomBookByCategory(category.getId(), null), category, null));
            } else {
                userBooks.add(new UserBook(user, Book.getRandomBookByCategory(null, category.getId()), category, null));
            }
        }
        return ok(views.html.my.discoverResults.render(category, userBooks));
    }

    public Result feedback() {
        return ok(views.html.my.feedback.render());
    }

    public Result feedbackPost(Http.Request request) {
        String feedbackText = request.body().asFormUrlEncoded().get("feedbackText")[0];
        UserFeedback userFeedback = new UserFeedback(CommonUtil.getUser(request), feedbackText);
        userFeedback.save();
        return ok(views.html.my.feedback.render());
    }

    public Result profile(Http.Request request) {
        User user = CommonUtil.getUser(request);
        return ok(views.html.my.profile.render(user));
    }

    // Book related

    public Result shufflePost(Http.Request request, Long userBookId) {
        Transaction txn = DB.beginTransaction();
        User user = CommonUtil.getUser(request);
        UserBook userBook = UserBook.find.byId(userBookId);
        userBook.setBook(Book.getRandomBookByCategory(null, userBook.getCategory().getId()));
        userBook.update();
        txn.commit();

        return ok(views.html.my.home.render(user));
    }

    public Result favoritePost(Http.Request request, Long bookId) {
        User user = CommonUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, bookId);
        if(userBook.favorited == null) {
            userBook.setFavorited(new Date());
        } else {
            userBook.setFavorited(null);
        }
        userBook.update();
        return ok(views.html.my.home.render(user));
    }

    public Result deletePost(Http.Request request, Long bookId) {
        User user = CommonUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, bookId);
        if(userBook != null) {
            userBook.delete();
        }
        return ok(views.html.my.home.render(user));
    }

    public Result list(Http.Request request, String listName) {
        List<UserBook> userBooks;
        User user = CommonUtil.getUser(request);
        if("favorites".equals(listName)) {
            userBooks = UserBook.findFavoriteUserBooks(user.id);
        } else if("recent".equals(listName)) {
            listName = "Recently Viewed";
            userBooks = UserBook.findRecentlyAccessedBooks(user.id);
        } else {
            userBooks = new ArrayList<>();
            /*Category subcategory = Category.findByTitle(listName);
            userBooks = UserBook.findPastUserBooksBySubCategory(subcategory.title, user.id);*/
        }
        return ok(views.html.my.list.render(listName, userBooks));
    }

    public Result categoryList(Http.Request request, Long id) {
        User user = CommonUtil.getUser(request);
        Category category = Category.find(id);
        List<UserBook> userBooks = UserBook.findUserBooksByCategory(user.getId(), category.getId());
        return ok(views.html.my.list.render(category.getTitle(), userBooks));
    }

    public Result book(Http.Request request, Long id) {
        User user = CommonUtil.getUser(request);
        Book book = Book.find.byId(id);
        UserBook userBook = null;
        if(user != null) {
            userBook = UserBook.findByUserAndBookId(user.id, id);
            if (userBook == null) {
                userBook = new UserBook(user, book, book.getCategory(), null);
            }
            userBook.setAccessed(new Date());
            userBook.saveOrUpdate();
        }

        List<Book> sameSubCategoryBooks = new ArrayList<>();
        sameSubCategoryBooks.add(book);
        sameSubCategoryBooks.add(book);
        sameSubCategoryBooks.add(book);
        /*for(int i = 1; i <= 3; i++) {
            sameSubCategoryBooks.add(Book.getRandomBookByCategory(null, userBook.getCategory().getId()));
        }*/
        List<Book> sameAuthorBooks = sameSubCategoryBooks;

        return ok(views.html.my.book.render(user, book, userBook, sameAuthorBooks, sameSubCategoryBooks));
    }

    public Result bookUserRatingPost(Http.Request request, Long bookId) {
        User user = CommonUtil.getUser(request);
        Book book = Book.find(bookId);
        UserRating userRating = UserRating.findByUserAndBookId(user.id, book.id);
        if(userRating == null) {
            userRating = new UserRating(user, book);
        }
        String rating = CommonUtil.getRequestBodyParam(request, "rating");
        if(StringUtils.isNotBlank(rating)) {
            userRating.setRating(Integer.valueOf(rating));
        }
        String text = CommonUtil.getRequestBodyParam(request, "text");
        if(StringUtils.isNotBlank(text)) {
            userRating.setText(text);
        }
        userRating.saveOrUpdate();
        return book(request, bookId);
    }

    public Result bookSearch(Http.Request request) {
        String searchTerm = CommonUtil.getRequestBodyParam(request, "searchTerm");
        List<Book> books = Book.search(searchTerm);
        return ok(views.html.my.bookSearchResults.render(books));
    }
}