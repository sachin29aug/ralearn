package controllers;

import models.*;
import play.api.libs.crypto.CookieSigner;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.CommonUtil;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
        String firstname = CommonUtil.getRequestBodyParam(request, "firstname");
        String lastname = CommonUtil.getRequestBodyParam(request, "lastname");
        String email = CommonUtil.getRequestBodyParam(request, "email");
        String password = CommonUtil.getRequestBodyParam(request, "password");

        User user = new User();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPassword(cookieSigner.sign(password));
        user.save();

        String categoryIds = request.body().asFormUrlEncoded().get("subcategoryIds")[0];
        for(String categoryId : categoryIds.split(",")) {
            UserCategory userCategory = new UserCategory(user, Category.find(Long.valueOf(categoryId)));
            userCategory.save();
        }

        return ok(views.html.visitor.welcome.render(user)).addingToSession(request, "email", user.getEmail()).addingToSession(request, "userId", String.valueOf(user.id));
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
                UserBook.generateRandomUserBooks(user, true, false);
            }

            return ok(views.html.my.home.render(user)).addingToSession(request, "email", user.getEmail()).addingToSession(request, "userId", String.valueOf(user.id));
        }
    }

    public Result forgotPassword() {
        return ok(views.html.visitor.forgotPassword.render());
    }

    public Result forgotPasswordPost(Http.Request request) {
        String email = CommonUtil.getRequestBodyParam(request, "email");
        User user = User.findByEmail(email);
        if(user != null) {
            user.setPasswordResetToken(UUID.randomUUID().toString());
            user.update();
            System.out.println("/reset-password/" + user.getPasswordResetToken());
        }
        return ok(views.html.visitor.login.render(null));
    }

    public Result resetPassword(String token) {
        if (User.findByPasswordResetToken(token) == null) {
            return badRequest("Invalid or expired reset link.");
        }
        return ok(views.html.visitor.resetPassword.render());
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
