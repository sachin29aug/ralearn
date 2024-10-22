package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Signup extends Controller {

    public Result categories() {
        return ok(views.html.signup.categories.render());
    }
}
