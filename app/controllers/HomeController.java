package controllers;

import models.Book;
import org.apache.pekko.japi.Pair;
import play.mvc.*;
import utils.GoogleBookClient;

import java.util.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
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

        List<Book> randomBooks = new ArrayList<>();
        Map<String, List<String>> categoriesMap = new LinkedHashMap<>();
        categoriesMap.put("Personal Development", Arrays.asList("self-help", "productivity", "communication-skills", "creativity", "education", "biography", "philosophy"));
        categoriesMap.put("Mind & Spirit", Arrays.asList("psychology", "spirituality", "mindfulness"));
        categoriesMap.put("Business & Economics", Arrays.asList("business", "economics", "leadership", "entrepreneurship", "marketing"));
        categoriesMap.put("Family & Lifestyle", Arrays.asList("childrens", "parenting", "travel"));
        categoriesMap.put("Science & Environment", Arrays.asList("science", "environment", "gardening"));
        categoriesMap.put("Arts & Humanities", Arrays.asList("art", "design", "architecture", "folklore", "history", "politics", "law"));

        List<String> subCategories = categoriesMap.get(category);

        for(String subCategory : subCategories) {
            int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).findCount();
            int randomIndex = new Random().nextInt(subCategoryBooksCount);
            Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
            Pair<String, String> pair = GoogleBookClient.getCoverImageUrlAndIsbn(randomBook.title);
            randomBook.coverImageUrl = pair.first();
            randomBook.isbn = pair.second();
            randomBook.update();
            randomBooks.add(randomBook);
        }

        return ok(views.html.books.render(category, randomBooks));
    }

    // Static utility methods

    public static String getVersionedUrl(play.api.mvc.Call url) {
        return getVersionedUrl(url.toString());
    }

    public static String getVersionedUrl(String url) {
        return url + "?v=1";
    }

}
