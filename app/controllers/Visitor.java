package controllers;

import models.*;
import play.api.libs.crypto.CookieSigner;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public Result signup() {
        return ok(views.html.visitor.signup.render(Category.findParentCategories()));
    }

    public Result signupLoginPost(Http.Request request) {
        String email = request.body().asFormUrlEncoded().get("email")[0];
        String password = request.body().asFormUrlEncoded().get("password")[0];
        String categoryIds = request.body().asFormUrlEncoded().get("subcategoryIds")[0];

        User user = new User(email, cookieSigner.sign(password));
        user.save();

        for(String categoryId : categoryIds.split(",")) {
            UserCategory userCategory = new UserCategory(user, Category.find(Long.valueOf(categoryId)));
            userCategory.save();
        }

        return ok(views.html.visitor.welcome.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
    }

    // Login related

    public Result login(String returnUrl) {
        return ok(views.html.visitor.login.render(returnUrl == null ? "" : returnUrl));
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

            return ok(views.html.my.home.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
        }
    }

    public Result logout(Http.Request request) {
        request.session().removing("email", "userId");
        // TODO: The PLAY_SESSION cookie is not being cleared with withNewSession(). Need to fix that.
        return redirect(routes.Visitor.login(null)).withNewSession();
    }

    // Legacy

    public Result legacy(Long parentCategoryId) {
        String parentCategory = "";
        if(parentCategoryId == 1) {
            parentCategory = "Personal Development";
        } else if (parentCategoryId == 2) {
            parentCategory = "Mind & Spirit";
        } else if (parentCategoryId == 3) {
            parentCategory = "Business & Economics";
        } else if (parentCategoryId == 4) {
            parentCategory = "Family & Lifestyle";
        } else if (parentCategoryId == 5) {
            parentCategory = "Science & Environment";
        } else if (parentCategoryId == 6) {
            parentCategory = "Arts & Humanities";
        } else {
            parentCategory = "Personal Development";
        }

        List<Book> randomBooks = new ArrayList<>();
        List<Category> categories = Category.findCategoriesByParent(Long.valueOf(parentCategoryId));
        for(Category category : categories) {
            Book randomBook = Book.getRandomBookByCategory(null, category.getId());
            randomBooks.add(randomBook);
        }

        return ok(views.html.visitor.legacy.render(parentCategory, randomBooks));
    }
}
