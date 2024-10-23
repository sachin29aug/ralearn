package controllers;

import io.ebean.DB;
import models.Book;
import models.Category;
import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.ComputerRepository;
import utils.GoogleBookClient;

import java.util.*;

public class My extends Controller {
    /*@Inject
    private ComputerRepository computerRepository;*/

    /*@Inject
    public My(ComputerRepository computerRepository) {
        this.computerRepository = computerRepository;
    }*/

    public Result home(Long categoryId) {
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
        Map<String, List<String>> categoriesMap = Category.getCategoriesMap();
        List<String> subCategories = categoriesMap.get(category);
        for(String subCategory : subCategories) {
            //int subCategoryBooksCount = Book.find.query().where().eq("sub_category", subCategory).gt("average_rating", 3.6).gt("rating_count", 1000).findCount();
            //int randomIndex = new Random().nextInt(subCategoryBooksCount);
            //Book randomBook = Book.findByOrderIndex(subCategory, randomIndex);
            Book randomBook = DB.find(Book.class).where().eq("subCategory", subCategory).gt("averageRating", 3.7).gt("ratingCount", 10000).setMaxRows(1).orderBy("RANDOM()").findOne();
            Map<String, String> map = GoogleBookClient.getCoverImageUrlAndIsbn(randomBook.title);
            randomBook.coverImageUrl = map.get("coverImageUrl");
            randomBook.isbn = map.get("isbn");
            randomBook.description = map.get("description");
            randomBook.previewUrl = map.get("previewUrl");
            randomBook.authorDescription = map.get("authorDescription");
            ComputerRepository.update(randomBook.id, randomBook.coverImageUrl, randomBook.isbn, randomBook.description, randomBook.previewUrl, randomBook.authorDescription);
            //randomBook.update();

            randomBooks.add(randomBook);
        }

        return ok(views.html.home.render(category, randomBooks));
    }

    public Result home1(Http.Request request) {
        String userId = request.session().get("userId").get();
        return ok(views.html.my.home1.render(User.find(userId)));
    }
}
