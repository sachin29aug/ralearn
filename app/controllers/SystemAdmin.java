package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.DB;
import io.ebean.DuplicateKeyException;
import io.ebean.Transaction;
import jakarta.persistence.PersistenceException;
import models.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.postgresql.util.PSQLException;
import play.mvc.Controller;
import play.mvc.Result;
import utils.CommonUtil;
import utils.GoogleBookClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdmin extends Controller {

    // Data Import related

    public Result importBooksGR() throws IOException {
        //Transaction txn = DB.beginTransaction();
        List<Category> categories = Category.findCategories();
        String confDir = Paths.get("conf").toAbsolutePath().toString();
        for(Category category : categories) {
            String categoryFilePath = "datasets/" + category.getParent().getUrl() + "/" + category.getUrl() + ".html";
            File categoryFile = new File(confDir, categoryFilePath);
            Document doc = Jsoup.parse(categoryFile, "UTF-8");
            Elements bookElements = doc.select("div.left[style='width: 75%;']");
            System.out.println("Category: " + category.getTitle() + ", Total Records: " + bookElements.size());
            int recordsProcessedCount = 0;
            for (Element bookElement : bookElements) {
                Element titleElement = bookElement.selectFirst("a.bookTitle");
                String title = titleElement != null ? titleElement.text().replaceAll("\\s*\\([^)]*\\)$", "").trim() : null;
                if (title.length() > 250) {
                    title = title.substring(0, 250);
                }
                String goodReadsUrl = titleElement != null ? titleElement.attr("href") : null;
                Element authorElement = bookElement.selectFirst("a.authorName");
                String author = authorElement != null ? authorElement.text() : null;
                Element greyTextElement = bookElement.selectFirst("span.greyText.smallText:contains(avg rating)");
                String greyText = greyTextElement != null ? greyTextElement.text() : null;
                BigDecimal rating = null;
                Integer ratingCount = null;
                String publishDate = null;
                if (greyText != null) {
                    String[] parts = greyText.split("—");
                    try {
                        rating = new BigDecimal(parts[0].replace("avg rating", "").trim());
                        ratingCount = parts.length > 1 ? Integer.valueOf(parts[1].replace("ratings", "").replace(",", "").trim()) : null;
                    } catch (NumberFormatException e) {
                    }
                    publishDate = parts.length > 2 ? parts[2].replace("published", "").trim() : null;
                }

                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setRating(rating);
                book.setRatingCount(ratingCount);
                book.setPublished(publishDate);
                book.setGrUrl(goodReadsUrl);
                //book.setCategory(category);
                try {
                    book.save();
                } catch (PersistenceException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof PSQLException && cause.getMessage().contains("duplicate key value violates unique constraint")) {
                        book = Book.findByTitleAndAuthor(title, author);
                    } else {
                        throw ex;
                    }
                }

                BookCategory bookCategory = new BookCategory(book, category);
                bookCategory.save();

                recordsProcessedCount++;
            }
            System.out.println("Category: " + category.getTitle() + ", Records Processed: " + recordsProcessedCount);
            System.out.println();
        }

        //txn.commit();
        System.out.printf("Done");
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
            BufferedReader reader = new BufferedReader(new FileReader("C:\\basedir\\env\\open-library\\ol_dump_works_2024-09-30\\ol_dump_works_2024-09-30.txt"));
            String line;
            int index = 0;
            Set<String> bookTitlesSet = new HashSet<>();
            for(Book book : Book.find.all()) {
                bookTitlesSet.add(book.getTitle());
                System.out.println("Book titles set populated");
            }

            while ((line = reader.readLine()) != null) {
                index++;
                if(index == count) {
                    reader.close();
                    break;
                }
                System.out.println("Index: " + index);
                String[] parts = line.split("\t", 5);
                if (parts.length < 5) continue;
                String jsonData = parts[4];
                JSONObject record = new JSONObject(jsonData);
                String title = record.optString("title", null);
                if (StringUtils.isBlank(title)) {
                    continue;
                } else {
                    title = title.trim();
                    if(!bookTitlesSet.contains(title)) {
                        continue;
                    }
                }

                Book book = Book.findByTitle(title);

                System.out.printf("Processed: " + index);

                String workKey = record.optString("key", null);
                String description = record.optJSONObject("description") != null ? record.getJSONObject("description").optString("value", null) : null;
                JSONArray covers = record.optJSONArray("covers");
                String coverId = covers != null && covers.length() > 0 ? String.valueOf(covers.getInt(0)) : null;
                JSONArray authors = record.optJSONArray("authors");
                String authorKey = null;
                if (authors != null && authors.length() > 0) {
                    JSONObject authorObject = authors.getJSONObject(0).optJSONObject("author");
                    if (authorObject != null) {
                        authorKey = authorObject.optString("key", null);
                    }
                }

                OLBook olBook = new OLBook();
                olBook.setWorkKey(workKey);
                olBook.setTitle(title);
                olBook.setCoverId(coverId);
                olBook.setDescription(description);
                olBook.setAuthorKey(authorKey);
                olBook.save();
                book.setOlBook(olBook);
                book.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("Done");
    }

    public Result importAuthorsOL() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("C:\\basedir\\env\\open-library\\ol_dump_authors_2024-09-30\\ol_dump_authors_2024-09-30.txt")); // This has 13652489 records and populates 28534 records in the ol_author table with bios
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                index++;
                System.out.println(index);

                String[] parts = line.split("\t");
                if (parts.length >= 5) {
                    String jsonPart = parts[4].trim();
                    JSONObject jsonObject = new JSONObject(jsonPart);

                    String bio = null;
                    JSONObject bioObject = jsonObject.optJSONObject("bio");
                    if (bioObject != null) {
                        bio = bioObject.optString("value");
                    }
                    if (StringUtils.isNotBlank(bio)) {
                        String key = jsonObject.optString("key");
                        String name = jsonObject.optString("name");
                        String photoId = null;
                        if (jsonObject.has("photos")) {
                            JSONArray photos = jsonObject.getJSONArray("photos");
                            if (photos.length() > 0) {
                                photoId = "" + photos.getInt(0);
                            }
                        }

                        OLAuthor olAuthor = new OLAuthor();
                        olAuthor.setAuthorKey(key);
                        olAuthor.setName(name);
                        olAuthor.setBio(bio);
                        olAuthor.setPhotoId(photoId);
                        olAuthor.save();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
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
            Category parentCategory = saveCategory("Personal Development", "fas fa-user-graduate", null);
            parentCategory.save();
            saveCategory("Self Help", "fas fa-heart", parentCategory);
            saveCategory("Productivity", "fas fa-tasks", parentCategory);
            saveCategory("Communication Skills", "fas fa-comments", parentCategory);
            saveCategory("Creativity", "fas fa-lightbulb", parentCategory);
            saveCategory("Education", "fas fa-graduation-cap", parentCategory);
            saveCategory("Biography", "fas fa-user", parentCategory);
            saveCategory("Philosophy", "fas fa-book", parentCategory);

            parentCategory = saveCategory("Mind & Spirit", "fas fa-brain", null);
            saveCategory("Psychology", "fas fa-brain", parentCategory);
            saveCategory("Spirituality", "fas fa-leaf", parentCategory);
            saveCategory("Mindfulness", "fas fa-seedling", parentCategory);

            parentCategory = saveCategory("Business & Economics", "fas fa-briefcase", null);
            saveCategory("Business", "fas fa-briefcase", parentCategory);
            saveCategory("Economics", "fas fa-chart-line", parentCategory);
            saveCategory("Leadership", "fas fa-users", parentCategory);
            saveCategory("Entrepreneurship", "fas fa-lightbulb", parentCategory);
            saveCategory("Marketing", "fas fa-bullhorn", parentCategory);

            parentCategory = saveCategory("Family & Lifestyle", "fas fa-home", null);
            saveCategory("Childrens", "fas fa-child", parentCategory);
            saveCategory("Parenting", "fas fa-baby", parentCategory);
            saveCategory("Travel", "fas fa-plane", parentCategory);

            parentCategory = saveCategory("Science & Environment", "fas fa-flask", null);
            saveCategory("Science", "fas fa-flask", parentCategory);
            saveCategory("Environment", "fas fa-tree", parentCategory);
            saveCategory("Gardening", "fas fa-leaf", parentCategory);

            parentCategory = saveCategory("Arts & Humanities", "fas fa-palette", null);
            saveCategory("Art", "fas fa-palette", parentCategory);
            saveCategory("Design", "fas fa-pencil-ruler", parentCategory);
            saveCategory("Architecture", "fas fa-building", parentCategory);
            saveCategory("Folklore", "fas fa-feather-alt", parentCategory);
            saveCategory("History", "fas fa-landmark", parentCategory);
            saveCategory("Politics", "fas fa-balance-scale", parentCategory);
            saveCategory("Law", "fas fa-gavel", parentCategory);

            txn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return ok(e.getMessage());
        }
        return ok("Done");
    }

    private Category saveCategory(String title, String faIconClass, Category parent) {
        Category category = new Category();
        category.setTitle(title);
        category.setUrl(CommonUtil.slugify(title));
        category.setFaIconClass(faIconClass);
        category.setParent(parent);
        category.save();
        return category;
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