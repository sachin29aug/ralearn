package controllers;

import models.User;
import models.UserBook;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.SessionUtil;

public class BookController extends Controller {
    public Result book(Http.Request request, Long id) {
        User user = SessionUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, id);
        return ok(views.html.book.render(models.Book.find.byId(id), userBook));
    }
}
