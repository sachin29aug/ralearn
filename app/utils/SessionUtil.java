package utils;

import models.User;
import play.mvc.Controller;
import play.mvc.Http;

public class SessionUtil extends Controller {
    public static User getUser(Http.Request request) {
        String userId = getUserId(request);
        return User.find(userId);
    }

    public static String getUserId(Http.Request request) {
        return request.session().get("userId").get();
    }
}
