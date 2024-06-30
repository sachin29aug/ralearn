package controllers;

import models.Book;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;

public class My extends Controller {
    public Result home() {
        List<Book> books = new ArrayList<>();
        return ok(views.html.home.render());
    }
}
