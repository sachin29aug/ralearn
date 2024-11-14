package controllers;

import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;
import models.Category;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GoogleBookClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdmin extends Controller {

    // Content related

    public Result importBooks() throws IOException {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        Map<String, List<String>> categoriesMap = new LinkedHashMap<>();
        categoriesMap.put("Personal Development", Arrays.asList("self-help", "productivity", "communication-skills", "creativity", "education", "biography", "philosophy"));
        categoriesMap.put("Mind & Spirit", Arrays.asList("psychology", "spirituality", "mindfulness"));
        categoriesMap.put("Business & Economics", Arrays.asList("business", "economics", "leadership", "entrepreneurship", "marketing"));
        categoriesMap.put("Family & Lifestyle", Arrays.asList("childrens", "parenting", "travel"));
        categoriesMap.put("Science & Environment", Arrays.asList("science", "environment", "gardening"));
        categoriesMap.put("Arts & Humanities", Arrays.asList("art", "design", "architecture", "folklore", "history", "politics", "law"));

        for(Map.Entry<String, List<String>> entry : categoriesMap.entrySet()) {
            String category = entry.getKey();
            List<String> subCategories = entry.getValue();
            for (String subCategory: subCategories) {
                List<Book> books = importBooks1(category, subCategory);
                for(Book book: books) {
                    sb.append(++count + "\n");
                    sb.append(book.category + "\n");
                    sb.append(book.subCategory + "\n");
                    sb.append(book.title + "\n");
                    sb.append(book.author  + "\n");
                    sb.append(book.averageRating  + "\n");
                    sb.append(book.ratingCount  + "\n");
                    sb.append(book.publishDate  + "\n");
                    sb.append(book.goodReadsUrl);
                    sb.append("========================"  + "\n\n");
                    book.save();
                }
            }
        }

        return ok(sb.toString());
    }

    public Result scrapBlinkCategories() {
        try {
            Document doc = Jsoup.connect("https://www.blinkist.com")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            Elements elements = doc.select("div.flex.w-full.items-center.font-medium");
            for (Element element : elements) {
                String value = element.text();
                System.out.println(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("");
    }

    public Result importGoogleBooksInfo(Long count) {
        try {
            Book book = Book.find.byId(36882L);
            GoogleBookClient.importGoogleBookInfo(book);
            //long i = 0;
            /*for(Book book: Book.find.all()) {
                GoogleBookClientV2.importGoogleBookInfo(book);
                int waitSecs = new Random().nextInt(6);
                Thread.sleep(waitSecs * 1000);

                i++;
                if(i == count) {
                    break;
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("Done");
    }

    //

    public static String getRandomBookLink() throws IOException {
        List<Book> books = importBooks1("Personal Development", "self-help");
        int randomIndex = new Random().nextInt(books.size());
        return books.get(randomIndex).goodReadsUrl;
    }

    public static List<Book> importBooks1(String category, String subCategory) throws IOException {
        String confDir = Paths.get("conf").toAbsolutePath().toString();
        String filePath = "datasets/" + category + "/" + subCategory + ".html";
        File inputFile = new File(confDir, filePath);
        Document doc = Jsoup.parse(inputFile, "UTF-8");

        List<Book> books = new ArrayList<>();
        long orderIndex = 0;
        for (Element htmlTag : doc.select("html")) {
            Elements bookTitles = htmlTag.select("a.bookTitle");
            Elements bookElements = htmlTag.select("a.leftAlignedImage");
            Elements authorNames = htmlTag.select("a.authorName > span");
            Elements details = htmlTag.select("span.greyText.smallText");

            for (int i = 0; i < bookTitles.size(); i++) {
                String bookTitle = bookElements.get(i).attr("title");
                String authorName = authorNames.get(i).text();
                String detailText = details.get(i).text();
                String[] parts = detailText.split("—");
                //Float avgRating = parts[0].replace("avg rating ", "").trim();
                Float avgRating = 1.1f;
                //String ratingsCount = "";
                Integer ratingsCount = 1;
                String publishDate = "";
                if(parts.length > 1) {
                    //ratingsCount = parts[1].replace("ratings", "").trim();
                    publishDate = parts[2].replace("published", "").trim();
                }
                String goodReadsLink = bookElements.get(i).attr("href");
                //books.add(new Book(bookTitle, authorName, avgRating, ratingsCount, publishDate, goodReadsLink, category, subCategory, null, null, orderIndex++));
            }
        }
        return books;
    }

    // Data Setup related

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