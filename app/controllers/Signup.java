package controllers;

import models.Category;
import models.User;
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
        User user = new User(email, password);
        user.save();
        return ok(views.html.signup.welcome.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
    }
}
