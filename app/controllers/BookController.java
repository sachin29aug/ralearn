package controllers;

import models.Book;
import models.User;
import models.UserBook;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.SessionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookController extends Controller {
    public Result book(Http.Request request, Long id) {
        User user = SessionUtil.getUser(request);
        UserBook userBook = UserBook.findByUserAndBookId(user.id, id);
        if (userBook != null) {
            userBook.setLastAccessed(new Date());
            userBook.update();
        }

        Book book = Book.find.byId(id);
        List<Book> sameAuthorBooks = new ArrayList<>();
        List<Book> sameSubCategoryBooks = new ArrayList<>();
        for(int i = 1; i <= 3; i++) {
            sameSubCategoryBooks.add(Book.getRandomBookByCategory(null, book.getSubCategory()));
        }
        sameAuthorBooks = sameSubCategoryBooks;

        return ok(views.html.book.render(book, userBook, sameAuthorBooks, sameSubCategoryBooks));
    }
}
