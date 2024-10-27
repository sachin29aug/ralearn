package controllers;

import models.Book;
import models.Category;
import org.apache.pekko.japi.Pair;
import play.mvc.*;
import repository.BookRepository;
import utils.GoogleBookClient;

import java.util.*;

public class HomeController extends Controller {

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result explore() {
        List<Book> books = new ArrayList<>();
        return ok(views.html.explore.render());
    }

    public Result books(Long categoryId) {
        String category = "";
        if(categoryId == 1) {
            category = "Personal Development";
        } else if (categoryId == 2) {
            category = "Mind & Spirit";
        } else if (categoryId == 3) {
            category = "Business & Economics";
        } else if (categoryId == 4) {
            category = "Family & Lifestyle";
        } else if (categoryId == 5) {
            category = "Science & Environment";
        } else if (categoryId == 6) {
            category = "Arts & Humanities";
        } else {
            category = "Personal Development";
        }

        Map<String, List<String>> categoriesMap = Category.getCategoriesMap();
        List<String> subCategories = categoriesMap.get(category);
        List<Book> randomBooks = new ArrayList<>();
        for(String subCategory : subCategories) {
            int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).findCount();
            int randomIndex = new Random().nextInt(subCategoryBooksCount);
            Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
            Map<String, String> map = GoogleBookClient.getCoverImageUrlAndIsbn(randomBook.title);
            randomBook.coverImageUrl = map.get("coverImageUrl");
            randomBook.isbn = map.get("isbn");
            //randomBook.update();
            randomBooks.add(randomBook);
        }

        return ok(views.html.books.render(category, randomBooks));
    }

    public Result book(Long id) {
        return ok(views.html.book.render(Book.find.byId(id)));
    }

    // Static utility methods

    public static String getVersionedUrl(play.api.mvc.Call url) {
        return getVersionedUrl(url.toString());
    }

    public static String getVersionedUrl(String url) {
        return url + "?v=8";
    }

}
