package controllers;

import models.User;
import models.UserBook;
import play.api.libs.crypto.CookieSigner;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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
}
