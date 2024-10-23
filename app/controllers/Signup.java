package controllers;

import models.Category;
import models.User;
import models.UserCategory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class Signup extends Controller {
    public Result categories() {
        return ok(views.html.signup.categories.render(Category.findParentCategories()));
    }

    public Result loginPost(Http.Request request) {
        String email = request.body().asFormUrlEncoded().get("email")[0];
        String password = request.body().asFormUrlEncoded().get("password")[0];
        String subcategoryIds = request.body().asFormUrlEncoded().get("subcategoryIds")[0];

        User user = new User(email, password);
        user.save();

        for(String subcategoryId : subcategoryIds.split(",")) {
            UserCategory userCategory = new UserCategory(user, Category.find(subcategoryId));
            userCategory.save();
        }

        return ok(views.html.signup.welcome.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
    }
}
