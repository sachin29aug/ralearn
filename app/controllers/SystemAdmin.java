package controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdmin extends Controller {
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
                    sb.append(book.avgRating  + "\n");
                    sb.append(book.ratingsCount  + "\n");
                    sb.append(book.publishDate  + "\n");
                    sb.append("<a href='https://www.goodreads.com/" + book.goodReadsLink  + "' target='_blank'>" + book.title + "</a>"  + "\n");
                    sb.append("========================"  + "\n\n");
                }
            }
        }

        return ok(sb.toString());
    }

    public static String getRandomBookLink() throws IOException {
        List<Book> books = importBooks1("Personal Development", "self-help");
        int randomIndex = new Random().nextInt(books.size());
        return books.get(randomIndex).goodReadsLink;
    }

    public static List<Book> importBooks1(String category, String subCategory) throws IOException {
        String confDir = Paths.get("conf").toAbsolutePath().toString();
        String filePath = "datasets/" + category + "/" + subCategory + ".html";
        File inputFile = new File(confDir, filePath);
        Document doc = Jsoup.parse(inputFile, "UTF-8");

        List<Book> books = new ArrayList<>();
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
                String avgRating = parts[0].replace("avg rating ", "").trim();
                String ratingsCount = "";
                String publishDate = "";
                if(parts.length > 1) {
                    ratingsCount = parts[1].replace("ratings", "").trim();
                    publishDate = parts[2].replace("published", "").trim();
                }
                String goodReadsLink = "<a href='https://www.goodreads.com/" + bookElements.get(i).attr("href")  + "' target='_blank'>" + bookTitle + "</a>";
                books.add(new Book(bookTitle, authorName, avgRating, ratingsCount, publishDate, goodReadsLink, category, subCategory));
            }
        }
        return books;
    }
}

class Book {
    public String title;
    public String author;
    public String avgRating;
    public String ratingsCount;
    public String publishDate;
    public String goodReadsLink;
    public String category;
    public String subCategory;

    public Book(String title, String author, String avgRating, String ratingsCount, String publishDate, String goodReadsLink, String category, String subCategory) {
        this.title = title;
        this.author = author;
        this.avgRating = avgRating;
        this.ratingsCount = ratingsCount;
        this.publishDate = publishDate;
        this.goodReadsLink = goodReadsLink;
        this.category = category;
        this.subCategory = subCategory;
    }
}
