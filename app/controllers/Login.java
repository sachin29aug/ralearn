package controllers;

import models.User;
import org.apache.commons.lang3.StringUtils;
import play.api.libs.crypto.CookieSigner;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class Login extends Controller {
    private final CookieSigner cookieSigner;

    @Inject
    public Login(CookieSigner cookieSigner) {
        this.cookieSigner = cookieSigner;
    }

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
            return ok(views.html.my.home1.render(user)).addingToSession(request, "email", user.email).addingToSession(request, "userId", String.valueOf(user.id));
        }
    }
}
