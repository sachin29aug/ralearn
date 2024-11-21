package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.DB;
import io.ebean.Transaction;
import models.Book;
import models.Category;
import models.OLBook;
import models.Quote;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GoogleBookClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdmin extends Controller {

    // Data Import and load related

    public Result importBooksGR() throws IOException {
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
                String confDir = Paths.get("conf").toAbsolutePath().toString();
                String filePath = "datasets/" + category + "/" + subCategory + ".html";
                File inputFile = new File(confDir, filePath);
                Document doc = Jsoup.parse(inputFile, "UTF-8");

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
                        Book book = new Book(bookTitle, authorName, avgRating, ratingsCount, publishDate, goodReadsLink, category, subCategory, null, null, orderIndex++);
                        book.save();
                    }
                }
            }
        }

        return ok("Done");
    }

    public Result importBooksGB(Long count) {
        try {
            long i = 0;
            for(Book book: Book.find.all()) {
                GoogleBookClient.importGoogleBookInfo(book);
                Thread.sleep(1200);
                i++;
                if(i == count) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("Done");
    }

    public Result importBooksOL(Long count) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\basedir\\env\\open-library\\ol_dump_editions_2024-09-30\\ol_dump_editions_2024-09-30.txt"));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                String jsonPart = parts[4];
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(jsonPart);
                String key = root.path("key").asText(null);
                String title = root.path("title").asText(null);
                JsonNode authorsNode = root.path("authors");
                List<String> authorsList = new ArrayList<>();
                if (authorsNode.isArray()) {
                    for (JsonNode author : authorsNode) {
                        authorsList.add(author.path("key").asText(null));
                    }
                }
                String authors = String.join(", ", authorsList);
                String isbn10 = root.path("isbn_10").isArray() ? root.path("isbn_10").get(0).asText(null) : null;
                String isbn13 = root.path("isbn_13").isArray() ? root.path("isbn_13").get(0).asText(null) : null;
                String publishedDate = root.path("publish_date").asText(null);
                Integer pageCount = root.path("number_of_pages").asInt(0);
                String oclcNumber = root.path("oclc_numbers").isArray() ? root.path("oclc_numbers").get(0).asText(null) : null;

                if(title == null) {
                    continue;
                }

                Book book = Book.findByTitle(title.trim());
                if (book != null) {
                    OLBook olBook = new OLBook();
                    olBook.setKey(key);
                    olBook.setTitle(title);
                    olBook.setAuthor(authors);
                    olBook.setIsbn10(isbn10);
                    olBook.setIsbn13(isbn13);
                    olBook.setPublishedDate(publishedDate);
                    olBook.setPageCount(pageCount);
                    olBook.setOclcNumber(oclcNumber);
                    olBook.save();
                    book.setOlBook(olBook);
                    book.update();
                }

                index++;
                if(index == count) {
                    break;
                }
                System.out.println(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("Done");
    }

    public Result importQuotesKaggle() throws IOException {
        Transaction txn = DB.beginTransaction();
        String confDir = Paths.get("conf").toAbsolutePath().toString();
        String filePath = "datasets-1/quotes/quotes-kaggle-abirate.jsonl";
        File file = new File(confDir, filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        ObjectMapper objectMapper = new ObjectMapper();
        String line;
        while ((line = br.readLine()) != null) {
            try {
                Map<String, Object> quoteData = objectMapper.readValue(line, Map.class);
                Quote quote = new Quote();
                quote.text = ((String) quoteData.get("quote")).trim().replace("\"", "").replace("“", "").replace("”", "");
                quote.author = ((String) quoteData.get("author")).trim();
                quote.tags = String.join(",", (List<String>) quoteData.get("tags"));
                quote.save();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        txn.commit();
        return ok("Done");
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