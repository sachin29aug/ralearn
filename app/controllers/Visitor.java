package controllers;

import models.*;
import play.api.libs.crypto.CookieSigner;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.SessionUtil;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Visitor extends Controller {
    private final CookieSigner cookieSigner;

    @Inject
    public Visitor(CookieSigner cookieSigner) {
        this.cookieSigner = cookieSigner;
    }

    public Result index() {
        return redirect(routes.Visitor.login(null));
    }

    // Signup related

    public Result categories() {
        return ok(views.html.signup.categories.render(Category.findParentCategories()));
    }

    public Result signupLoginPost(Http.Request request) {
        String email = request.body().asFormUrlEncoded().get("email")[0];
        String password = request.body().asFormUrlEncoded().get("password")[0];
        String subcategoryIds = request.body().asFormUrlEncoded().get("subcategoryIds")[0];

        User user = new User(email, cookieSigner.sign(password));
        user.save();

        for(String subcategoryId : subcategoryIds.split(",")) {
            UserCategory userCategory = new UserCategory(user, Category.find(subcategoryId));
            userCategory.save();
        }

        return ok(views.html.signup.welcome.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
    }

    // Login related

    public Result login(String returnUrl) {
        return ok(views.html.login.login.render(returnUrl == null ? "" : returnUrl));
    }

    public Result loginPost(Http.Request request) {
        String email = request.body().asFormUrlEncoded().get("email")[0];
        String password = request.body().asFormUrlEncoded().get("password")[0];

        //String returnUrl = request.body().asFormUrlEncoded().get("returnUrl")[0];
        User user = User.authenticate(email, cookieSigner.sign(password));
        if (user == null) {
            return badRequest("Invalid user name or password");
        } else {
            String timezone = request.body().asFormUrlEncoded().get("timezone")[0];
            ZoneId userZoneId = ZoneId.of(timezone);
            LocalDate currentDateInUserZone = ZonedDateTime.now(userZoneId).toLocalDate();
            Date maxAssignedDate = UserBook.findMaxAssignedDate(user.id);
            LocalDate maxAssignedDateLocal = maxAssignedDate.toInstant().atZone(userZoneId).toLocalDate();
            if (currentDateInUserZone.isAfter(maxAssignedDateLocal)) {
                UserBook.generateUserBooks(user, true, false);
            }

            return ok(views.html.my.home1.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
        }
    }

    //

    public Result book(Http.Request request, Long id) {
        User user = SessionUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, id);
        if (userBook != null) {
            userBook.setLastAccessed(new Date());
            userBook.update();
        }

        Book book = Book.find.byId(id);
        List<Book> sameAuthorBooks = new ArrayList<>();
        List<Book> sameSubCategoryBooks = new ArrayList<>();
        for(int i = 1; i <= 3; i++) {
            sameSubCategoryBooks.add(Book.getRandomBookByCategory(null, book.getSubCategory()));
        }
        sameAuthorBooks = sameSubCategoryBooks;

        return ok(views.html.book.render(book, userBook, sameAuthorBooks, sameSubCategoryBooks));
    }
}
