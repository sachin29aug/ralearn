package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class BookController extends Controller {
    public Result book(Long id) {
        return ok(views.html.book.render(models.Book.find.byId(id)));
    }
}
