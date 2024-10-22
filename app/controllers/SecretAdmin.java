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

    public Result populateBooks() {
        try {
            Transaction txn = DB.beginTransaction();
            List<Book> books = Book.find.all();
            int count = 0;
            for (Book book : books) {
                //Pair<String, String> pair = GoogleBookClient.getCoverImageUrlAndIsbn(book.title);
                /*if(pair != null) {
                    book.coverImageUrl = pair.first();
                    book.isbn = pair.second();
                    book.update();
                    txn.commit();
                    txn = DB.beginTransaction();
                    System.out.println(count++);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
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
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            Category category2 = new Category("Mind & Spirit", "fas fa-brain", null);
            DB.save(category2);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category2);
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            Category category3 = new Category("Business & Economics", "fas fa-briefcase", null);
            DB.save(category3);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category3);
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            Category category4 = new Category("Family & Lifestyle", "fas fa-home", null);
            DB.save(category4);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category4);
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            Category category5 = new Category("Science & Environment", "fas fa-flask", null);
            DB.save(category5);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category5);
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            Category category6 = new Category("Art & Humanities", "fas fa-palette", null);
            DB.save(category6);
            for(String subCategoryName : subCategoryNames) {
                Category subCategory = new Category(subCategoryName, null, category6);
                setFaIconClass(subCategory);
                DB.save(subCategory);
            }

            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }

        return ok("Done");
    }

    private static void setFaIconClass(Category subCategory) {
        if (subCategory.title.equals("self-help")) {
            subCategory.faIconClass = "fas fa-heart"; // Heart icon for self-help
        } else if (subCategory.title.equals("productivity")) {
            subCategory.faIconClass = "fas fa-tasks"; // Tasks icon for productivity
        } else if (subCategory.title.equals("communication-skills")) {
            subCategory.faIconClass = "fas fa-comments"; // Comments icon for communication skills
        } else if (subCategory.title.equals("creativity")) {
            subCategory.faIconClass = "fas fa-lightbulb"; // Lightbulb icon for creativity
        } else if (subCategory.title.equals("education")) {
            subCategory.faIconClass = "fas fa-graduation-cap"; // Graduation Cap icon for education
        } else if (subCategory.title.equals("biography")) {
            subCategory.faIconClass = "fas fa-user"; // User icon for biography
        } else if (subCategory.title.equals("philosophy")) {
            subCategory.faIconClass = "fas fa-book"; // Book icon for philosophy
        } else if (subCategory.title.equals("psychology")) {
            subCategory.faIconClass = "fas fa-brain"; // Brain icon for psychology
        } else if (subCategory.title.equals("spirituality")) {
            subCategory.faIconClass = "fas fa-leaf"; // Leaf icon for spirituality
        } else if (subCategory.title.equals("mindfulness")) {
            subCategory.faIconClass = "fas fa-seedling"; // Seedling icon for mindfulness
        } else if (subCategory.title.equals("business")) {
            subCategory.faIconClass = "fas fa-briefcase"; // Briefcase icon for business
        } else if (subCategory.title.equals("economics")) {
            subCategory.faIconClass = "fas fa-chart-line"; // Chart Line icon for economics
        } else if (subCategory.title.equals("leadership")) {
            subCategory.faIconClass = "fas fa-users"; // Users icon for leadership
        } else if (subCategory.title.equals("entrepreneurship")) {
            subCategory.faIconClass = "fas fa-lightbulb"; // Lightbulb icon for entrepreneurship
        } else if (subCategory.title.equals("marketing")) {
            subCategory.faIconClass = "fas fa-bullhorn"; // Bullhorn icon for marketing
        } else if (subCategory.title.equals("childrens")) {
            subCategory.faIconClass = "fas fa-child"; // Child icon for children's books
        } else if (subCategory.title.equals("parenting")) {
            subCategory.faIconClass = "fas fa-baby"; // Baby icon for parenting
        } else if (subCategory.title.equals("travel")) {
            subCategory.faIconClass = "fas fa-plane"; // Plane icon for travel
        } else if (subCategory.title.equals("science")) {
            subCategory.faIconClass = "fas fa-flask"; // Flask icon for science
        } else if (subCategory.title.equals("environment")) {
            subCategory.faIconClass = "fas fa-tree"; // Tree icon for environment
        } else if (subCategory.title.equals("gardening")) {
            subCategory.faIconClass = "fas fa-leaf"; // Leaf icon for gardening
        } else if (subCategory.title.equals("art")) {
            subCategory.faIconClass = "fas fa-palette"; // Palette icon for art
        } else if (subCategory.title.equals("design")) {
            subCategory.faIconClass = "fas fa-pencil-ruler"; // Pencil Ruler icon for design
        } else if (subCategory.title.equals("architecture")) {
            subCategory.faIconClass = "fas fa-building"; // Building icon for architecture
        } else if (subCategory.title.equals("folklore")) {
            subCategory.faIconClass = "fas fa-feather-alt"; // Feather icon for folklore
        } else if (subCategory.title.equals("history")) {
            subCategory.faIconClass = "fas fa-landmark"; // Landmark icon for history
        } else if (subCategory.title.equals("politics")) {
            subCategory.faIconClass = "fas fa-balance-scale"; // Balance scale icon for politics
        } else if (subCategory.title.equals("law")) {
            subCategory.faIconClass = "fas fa-gavel"; // Gavel icon for law
        }
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
