package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.DB;
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

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdmin extends Controller {
    private static final String CONF_DIR = Paths.get("conf").toAbsolutePath().toString();

    // Data Import related

    public Result importBooksGR() throws IOException {
        //Transaction txn = DB.beginTransaction();
        List<Category> categories = Category.findCategories();
        for(Category category : categories) {
            String categoryFilePath = "datasets/" + category.getParent().getUrl() + "/" + category.getUrl() + ".html";
            File categoryFile = new File(CONF_DIR, categoryFilePath);
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
        String filePath = "datasets-1/quotes/quotes-kaggle-abirate.jsonl";
        File file = new File(CONF_DIR, filePath);
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

    public Result importBooksCPT() throws IOException {
        Transaction txn = DB.beginTransaction();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(CONF_DIR, "datasets-1/cpt/books-cpt-output.csv")));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].replaceAll("^\"|\"$", "");
                }

                long bookId = Long.valueOf(row[0].trim());
                Book book = Book.find(bookId);
                CPTBook cptBook = book.getCptBook();
                if(cptBook == null) {
                    cptBook = new CPTBook();
                }
                cptBook.setHeadline(row[3].trim());
                cptBook.setTeaser(row[4].trim());
                cptBook.setDescription(row[5].trim());
                cptBook.setAuthorBio(row[6].trim());
                cptBook.setThemeConcept(row[7].trim());
                cptBook.setAudience(row[8].trim());
                cptBook.setStyleTone(row[9].trim());
                cptBook.setActionableIdeas(row[10].trim());
                cptBook.setUsp(row[11].trim());
                cptBook.setTopics(row[12].trim());
                cptBook.saveOrUpdate();
                book.setCptBook(cptBook);
                book.update();

                String bookQuotes = row[13].trim();
                String authorQuotes = row[14].trim();
                String authorName = row[2].trim();
                if (bookQuotes != null && !bookQuotes.trim().isEmpty()) {
                    String[] quotes = bookQuotes.split("\\|");
                    for (String quoteText : quotes) {
                        Quote quote = new Quote();
                        quote.setText(quoteText.trim());
                        quote.setBook(book);
                        quote.save();
                    }
                }

                if (authorQuotes != null && !authorQuotes.trim().isEmpty()) {
                    String[] quotes = authorQuotes.split("\\|");
                    for (String quoteText : quotes) {
                        Quote quote = new Quote();
                        quote.setText(quoteText.trim());
                        quote.setBook(book);
                        quote.setAuthor(authorName);
                        quote.save();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        txn.commit();
        return ok("Done");
    }

    // Data Export related

    public Result exportBooksCPT() {
        // Prompt text
        StringBuilder sb = new StringBuilder();
        sb.append("I am building a book recommendation website and need concise, original content for each book to enhance user experience. Please generate the following details:\n")
            .append("Core Content:\n")
            .append("    headline: A catchy, engaging tagline summarizing the book in one sentence. (minimum 10 words)\n")
            .append("    teaser: A short summary (at least 20 words) capturing the essence of the book.\n")
            .append("    description: A longer, unique synopsis highlighting the book's key appeal. At least 4 paragraphs and should be informative. Enclose it in <p></p> so that I can render it as it is as html\n")
            .append("    author bio: A brief bio about the author\n")
            .append("    book quotes: 2-5 quotes from the book (non-spoiler, **minimum 20 words**). If quotes are short please provide excerpts from the book. \n")
            .append("    author quotes: 2-5 quotes from the author (non-spoiler, not necessarily related to this book, **minimum 20 words**).\n")
            .append("Additional Details:\n")
            .append("    theme and concept: (***at least 3 distinct points***, separated by |. I see you are providing 2 points, please provide at least 3).\n")
            .append("    audience: A concise description of who would enjoy or benefit from the book.\n")
            .append("    content style and tone: A brief description of the book's content Style and tone.\n")
            .append("    actionable ideas: Actionable tips or ideas (at least 3 points, separated by |)\n")
            .append("    usp: What sets this book apart from others in its genre.\n")
            .append("    topics: The various topics or tags this book belongs to. It could be anything like category, genre, topics etc. Please provide as many topics as you can, so that I can use this during the search implementation for the website\n")
            .append("Input Information: 5 book records directly pasted on the chat, at the end of the prompt, with following format: id, title slug, author\n")
            .append("Expected Output: The output should be of same format with additional columns and data. Please paste it here only in CSV format which I can simply copy and paste to a csv file. Please follow this order: id,titleSlug,author,headline,teaser,description,authorBio,themeConcept,audience,styleTone,actionableIdeas,usp,topics,bookQuotes,authorQuotes \n")
            .append("Notes:\n")
            .append("    Use | for fields like Concept, Book Quotes, Author Quotes, and Ideas to format as bullets in the UI.\n")
            .append("    Ensure all content is original, engaging, and plagiarism-free. Avoid direct excerpts or copyrighted text.\n")
            .append("    Please provide detailed, rich descriptions for the book titles in this CSV. I am okay with waiting if it requires processing in smaller batches. Please put stronger focus on generating dynamic, context-sensitive content for each book. I am ok it takes longer and  it requires more advanced logic, but I need quality content. Please generate high-quality, dynamic, context-sensitive content for each book.\n")
            .append("Ensure the `Concept` field always includes **exactly 3 distinct points separated by pipes (|)**. This is critical for proper implementation.\n")
            .append("\n");

        // Prompt data

        boolean attachment = false;
        ByteArrayOutputStream outputStream = null;
        PrintWriter writer = null;
        String csvHeader = "id,title slug,author\n";
        sb.append(csvHeader);
        if(attachment) {
            outputStream = new ByteArrayOutputStream();
            writer = new PrintWriter(outputStream);
            writer.print(csvHeader);
        }

        List<Book> books = Book.getRandomBooksCPT(5);
        for (Book book : books) {
            String csvRecord = String.format("%s,%s,%s%n", book.getId(), CommonUtil.slugify(book.getTitle()), book.getAuthor());
            if(attachment) {
                writer.print(csvRecord);
            }
            sb.append(csvRecord);
        }

        if(attachment) {
            writer.flush();
            return ok(outputStream.toByteArray()).as("text/csv").withHeader("Content-Disposition", "attachment; filename=books-cpt.csv");
        } else {
            return ok(sb.toString());
        }
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