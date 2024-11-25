package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.DB;
import io.ebean.Transaction;
import models.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
        // Todo: Multi author fix

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
                Elements bookElements = doc.select("div.left[style='width: 75%;']");
                for (Element bookElement : bookElements) {
                    Element titleElement = bookElement.selectFirst("a.bookTitle");
                    String title = titleElement != null ? titleElement.text() : null;
                    if(title.length() > 250) {
                        title = title.substring(0, 250);
                    }
                    String goodReadsUrl = titleElement != null ? titleElement.attr("href") : null;
                    Element authorElement = bookElement.selectFirst("a.authorName");
                    String authorName = authorElement != null ? authorElement.text() : null;
                    Element greyTextElement = bookElement.selectFirst("span.greyText.smallText");
                    String greyText = greyTextElement != null ? greyTextElement.text() : null;
                    Float rating = null;
                    Integer ratingCount = null;
                    String publishDate = null;
                    if (greyText != null) {
                        String[] parts = greyText.split("—");
                        try {
                            rating = Float.valueOf(parts[0].replace("avg rating", "").trim());
                            ratingCount = parts.length > 1 ? Integer.valueOf(parts[1].replace("ratings", "").replace(",", "").trim()) : null;
                        } catch (NumberFormatException e) {
                        }
                        publishDate = parts.length > 2 ? parts[2].replace("published", "").trim() : null;
                    }

                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(authorName);
                    book.setRating(rating);
                    book.setRatingCount(ratingCount);
                    book.setPublished(publishDate);
                    book.setGrUrl(goodReadsUrl);
                    book.setCategory(category);
                    book.setCategory(subCategory);
                    book.save();
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