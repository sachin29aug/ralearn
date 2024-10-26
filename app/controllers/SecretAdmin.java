package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;
import models.Category;
import org.apache.pekko.japi.Pair;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GoogleBookClient;

import java.util.List;
import java.util.Map;

public class SecretAdmin extends Controller {

    public Result test() throws Exception {
        //Pair pair = GoogleBookClient.getCoverImageUrlAndIsbn("The Little Book That Beats the Market");
        //return ok(pair.first() + "        " + pair.second());
        return null;
    }

    public Result setupCategories() {
        Transaction txn = DB.beginTransaction();
        try {
            Map<String, List<String>> categoriesMap = Category.getCategoriesMap();

            Category category1 = new Category("Personal Development", "fas fa-user-graduate", null);
            DB.save(category1);
            List<String> subCategoryNames = categoriesMap.get(category1.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category1);
                DB.save(subCategory);
            }

            Category category2 = new Category("Mind & Spirit", "fas fa-brain", null);
            DB.save(category2);
            subCategoryNames = categoriesMap.get(category2.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category2);
                DB.save(subCategory);
            }

            Category category3 = new Category("Business & Economics", "fas fa-briefcase", null);
            DB.save(category3);
            subCategoryNames = categoriesMap.get(category3.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category3);
                DB.save(subCategory);
            }

            Category category4 = new Category("Family & Lifestyle", "fas fa-home", null);
            DB.save(category4);
            subCategoryNames = categoriesMap.get(category4.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category4);
                DB.save(subCategory);
            }

            Category category5 = new Category("Science & Environment", "fas fa-flask", null);
            DB.save(category5);
            subCategoryNames = categoriesMap.get(category5.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category5);
                DB.save(subCategory);
            }

            Category category6 = new Category("Arts & Humanities", "fas fa-palette", null);
            DB.save(category6);
            subCategoryNames = categoriesMap.get(category6.title);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category6);
                DB.save(subCategory);
            }

            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
    }

    /* The wrapper code to refer when coding custom jobs
    public static Result xyzJob() {
        Transaction txn = DB.beginTransaction();
        try {
            // You code here..

            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
    } */
}
