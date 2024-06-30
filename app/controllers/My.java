package controllers;

import models.Book;
import org.apache.pekko.japi.Pair;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GoogleBookClient;

import java.util.*;

public class My extends Controller {
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

        return ok(views.html.home.render(category, randomBooks));
    }
}
