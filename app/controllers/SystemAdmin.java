package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.DB;
import io.ebean.Transaction;
import jakarta.persistence.PersistenceException;
import models.*;
import org.apache.commons.lang3.StringUtils;
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
    private static final String CONF_DATASETS_DIR = Paths.get("conf").toAbsolutePath().toString() + "/datasets";

    // Data Import related

    public Result importBooksGR() throws IOException {
        //Transaction txn = DB.beginTransaction();
        List<Category> categories = Category.findCategories();
        //List<Category> categories = new ArrayList<>();
        //categories.add(Category.findByUrl("mystery"));
        int totalRecordsScannedCount = 0;
        int booksScanned = 0;
        for(Category category : categories) {
            Document doc = Jsoup.parse(new File(CONF_DATASETS_DIR, "/books/gr/" + category.getParent().getUrl() + "/" + category.getUrl() + ".html"), "UTF-8");
            Elements bookElements = doc.select("div.left[style='width: 75%;']");
            System.out.println("Category: " + category.getTitle() + ", Total Records: " + bookElements.size());
            int recordsScannedCount = 0;
            for (Element bookElement : bookElements) {
                Element titleElement = bookElement.selectFirst("a.bookTitle");
                String title = titleElement != null ? titleElement.text().replaceAll("\\s*\\([^)]*\\)$", "").trim() : null;
                if (title.length() > 250) {
                    title = title.substring(0, 250);
                }
                String bookGrUrl = titleElement != null ? titleElement.attr("href").trim() : null;
                Element authorElement = bookElement.selectFirst("a.authorName");
                String authorName = authorElement != null ? authorElement.text() : null;
                String authorGrUrl = authorElement != null ? authorElement.attr("href").trim().replace("https://www.goodreads.com", "") : null;
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

                Author author = new Author();
                author.setName(authorName);
                author.setGrUrl(authorGrUrl);
                try {
                    author.save();
                } catch (PersistenceException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof PSQLException && cause.getMessage().contains("duplicate key value violates unique constraint")) {
                        author = Author.findByGrUrl(authorGrUrl);
                        if(author == null) {
                            System.out.printf(authorGrUrl);
                        }
                    } else {
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                Book book = Book.findByGrUrl(bookGrUrl);
                if(book != null) {
                    booksScanned++;
                    book.setAuthor(author);
                    book.update();
                } else {
                    System.out.println(bookGrUrl);
                }

                /*Book book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setRating(rating);
                book.setRatingCount(ratingCount);
                book.setPublished(publishDate);
                book.setGrUrl(bookGrUrl);
                try {
                    book.save();
                } catch (PersistenceException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof PSQLException && cause.getMessage().contains("duplicate key value violates unique constraint")) {
                        book = Book.findByTitleAndAuthor(title, author.getName());
                    } else {
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                BookCategory bookCategory = new BookCategory(book, category);
                bookCategory.save();*/

                recordsScannedCount++;
                totalRecordsScannedCount++;
                System.out.println(totalRecordsScannedCount);
            }
            System.out.println("Category: " + category.getTitle() + ", Records Processed: " + recordsScannedCount);
            System.out.println();
        }

        //txn.commit();
        System.out.println("totalRecordsScannedCount: " + totalRecordsScannedCount);
        System.out.println("booksScanned: " + booksScanned);
        System.out.println("Done");
        return ok("Done");
    }

    public Result importBookGB(Long bookId) {
        try {
            Book book = Book.find(bookId);
            GoogleBookClient.importGoogleBookInfo(book);
        } catch (Exception e) {
            e.printStackTrace();
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

    public Result importQuotesKaggle() throws IOException {
        Transaction txn = DB.beginTransaction();
        File file = new File(CONF_DATASETS_DIR, "/quotes/quotes-kaggle-abirate.jsonl");
        BufferedReader br = new BufferedReader(new FileReader(file));
        ObjectMapper objectMapper = new ObjectMapper();
        String line;
        while ((line = br.readLine()) != null) {
            try {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
                Map<String, Object> quoteData = objectMapper.readValue(line, typeRef);
                String quoteText = ((String) quoteData.get("quote")).trim().replace("\"", "").replace("“", "").replace("”", "");
                if(quoteText.length() <= CommonUtil.DISCARD_QUOTE_CHAR_LIMIT) {
                    Quote quote = new Quote();
                    quote.setText(quoteText);
                    quote.setAuthorName(((String) quoteData.get("author")).trim());
                    quote.setTags(String.join(",", (List<String>) quoteData.get("tags")));
                    quote.setSource(Quote.Source.KAGGLE);
                    quote.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        txn.commit();
        return ok("Done");
    }

    public Result importQuotesGR() throws IOException {
        Transaction txn = DB.beginTransaction();
        Document doc = Jsoup.parse(new File(CONF_DATASETS_DIR, "/quotes/quotes-gr.html"), "UTF-8");
        Elements quoteElements = doc.select(".quote");
        int count = 0;
        for (Element quoteElement : quoteElements) {
            String rawQuoteText = quoteElement.select(".quoteText").text().replace("\u201C", "").replace("\u201D", "");
            int lastDashIndex = rawQuoteText.lastIndexOf("―");
            String quoteText = rawQuoteText.substring(0, lastDashIndex).trim();
            String authorName = rawQuoteText.substring(lastDashIndex + 1).trim();
            String authorUrl = quoteElement.select(".quoteAvatar").attr("href").trim();
            Elements tagElements = quoteElement.select(".greyText.smallText.left a");
            String tags = tagElements.eachText().stream().reduce((tag1, tag2) -> tag1 + ", " + tag2).orElse("");
            int likesCount = Integer.parseInt(quoteElement.select(".right .smallText").text().split(" ")[0].replace(",", ""));

            if(quoteText.length() <= CommonUtil.DISCARD_QUOTE_CHAR_LIMIT) {
                Quote quote = new Quote();
                quote.setText(quoteText);
                quote.setAuthorName(authorName);
                quote.setTags(tags);
                quote.setLikesCount(likesCount);
                quote.setSource(Quote.Source.GR);
                if(StringUtils.isNotBlank(authorUrl) && authorUrl.startsWith("/author/show/")) {
                    Author author = Author.findByGrUrl(authorUrl);
                    if(author == null) {
                        author = new Author();
                        author.setName(authorName);
                        author.setGrUrl(authorUrl);
                        author.save();
                    }
                    quote.setAuthor(author);
                }
                quote.save();
            }

            System.out.println(++count);
        }

        txn.commit();
        return ok("Done");
    }

    public Result importBooksCPT() throws IOException {
        Transaction txn = DB.beginTransaction();
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(new File(CONF_DATASETS_DIR, "/books/books-cpt-output.csv")));
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
                cptBook.setHeadline(row[4].trim());
                cptBook.setTeaser(row[5].trim());
                cptBook.setDescription(row[6].trim());
                cptBook.setThemeConcept(row[7].trim());
                cptBook.setKeyTakeaways(row[8].trim());
                cptBook.setActionableIdeas(row[9].trim());
                cptBook.setAudience(row[10].trim());
                cptBook.setStyleTone(row[11].trim());
                cptBook.setUsp(row[12].trim());
                cptBook.setTopics(row[13].trim());
                cptBook.setSetting(row[14].trim());
                cptBook.setImpactfulPassages(row[15].trim());
                cptBook.saveOrUpdate();
                book.setCptBook(cptBook);
                book.update();

                long authorId = Long.valueOf(row[2].trim());
                Author author = Author.find(authorId);
                author.setBio(row[16].trim());
                author.update();

                String bookQuotes = row[17].trim();
                if (bookQuotes != null && !bookQuotes.trim().isEmpty()) {
                    String[] quotes = bookQuotes.split("\\|");
                    for (String quoteText : quotes) {
                        Quote quote = new Quote();
                        quote.setText(quoteText.trim());
                        quote.setBook(book);
                        quote.setSource(Quote.Source.CPT);
                        quote.save();
                    }
                }

                String authorQuotes = row[18].trim();
                if (authorQuotes != null && !authorQuotes.trim().isEmpty()) {
                    String[] quotes = authorQuotes.split("\\|");
                    for (String quoteText : quotes) {
                        Quote quote = new Quote();
                        quote.setText(quoteText.trim());
                        quote.setBook(book);
                        quote.setAuthor(Author.find(Long.valueOf(row[2].trim())));
                        quote.setSource(Quote.Source.CPT);
                        quote.save();
                    }
                }

                sb.append("<a target='_blank' href='http://localhost:9100/book/" + bookId + "'>" + book.getTitle() + "</a>\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        txn.commit();
        return ok(sb.toString().replaceAll("\\n", "<br>")).as("text/html");
    }

    // Data Export related

    public Result exportBooksCPT() {
        // Prompt text
        int RECORD_COUNT = 5;
        int BATCH_COUNT = 5;
        StringBuilder sb = new StringBuilder();
        sb.append("I am building a book recommendation website and need concise, original, and engaging content for each book to enhance user experience. Please generate the following details:\n")
            .append("    headline: A catchy, engaging tagline summarizing the book in one sentence. (minimum 10 words)\n")
            .append("    teaser: A short summary (at least 20 words) capturing the essence of the book.\n")
            .append("    description: A longer, unique synopsis highlighting the book's key appeal. At least 6 paragraphs and should be informative. Enclose it in <p> tags so that it can be rendered as-is in HTML.\n")
            .append("    theme and concept: Provide exactly 3 distinct, well-defined points separated by |. Each point should describe the overarching principles or abstract ideas that underpin the book’s message. Focus on the book's philosophy and core themes without diving into specific lessons or actions. Avoid generic phrasing, and ensure each point conveys a meaningful insight into the book’s essence.\n")
            .append("    key takeaways: Provide at least 3 specific and concise insights separated by |. Each takeaway should summarize the intellectual or conceptual value the book offers to the reader. Focus on what the reader will learn or understand from the book. Avoid prescribing specific actions here; instead, highlight the main lessons or unique frameworks the book provides.\n")
            .append("    actionable ideas: Provide exactly 3 practical, specific, and actionable steps separated by |. These ideas should focus on what readers can directly implement after reading the book. Tailor the suggestions to the book's themes, ensuring they are tangible and relatable. Avoid broad or vague advice.\n")
            .append("    audience: A concise description of who would enjoy or benefit from the book.\n")
            .append("    style, tone and mood: Provide a concise description (** 1-2 sentences **) blending the book’s narrative style and emotional atmosphere. Use vivid and descriptive language to ensure clarity and engagement. Highlight both how the book is written (style and tone) and how it makes the reader feel (mood), avoiding generic phrasing.\n")
            .append("    usp: What sets this book apart from others in its genre.\n")
            .append("    topics: Include a mix of broad and specific topics/tags (e.g., category, genre, themes, and concepts) to support search implementation.\n")
            .append("    setting: Populate this only if the book has a significant time, place, or cultural context, otherwise leave it blank.\n")
            .append("    impactful passages: Include powerful excerpts from the book, ensuring they are contextually relevant and non-spoiler. Provide at least 2 passages that resonate with the book’s themes.\n")
            .append("    author bio: Provide a brief bio about the author (at least 50 words).\n")
            .append("    book quotes: Provide 2-5 quotes from the book (non-spoiler, minimum 20 words). Ensure they are tied to the book’s themes or ideas. If quotes are short, provide contextually accurate excerpts from the book that directly relate to its theme, message, or core ideas. Ensure these quotes are not generic or unrelated and reflect the content of the book. Don't enclose these in single or double quotes. \n")
            .append("    author quotes: Provide 2-5 quotes from the author (non-spoiler, not necessarily related to this book, **minimum 20 words**). Ensure the quotes are either directly attributed to the author or are paraphrased insights that match their known perspective. Avoid generic quotes or misattributed statements.  Don't enclose these in single or double quotes.\n")
            .append("Input Information: 5 book records directly pasted on the chat, at the end of the prompt, with following format: bookId, titleSlug, authorId, authorName\n")
            .append("Expected Output: The output should be of same format with additional columns and data. Please paste it here only in CSV format which I can simply copy and paste to a csv file. Please follow this order: bookId,titleSlug,authorId,authorName,headline,teaser,description,themeConcept,keyTakeaways,actionableIdeas,audience,styleTone,,usp,topics,setting,impactfulPassages,authorBio,bookQuotes,authorQuotes \n")
            .append("Notes:\n")
            .append("    Enclose all the columns data inside double quotes except bookId and authorId. Please don't put any quotes within the quotes \n")
            .append("    Use | for fields like themeConcept, bookQuotes, authorQuotes, and actionableIdeas to format as bullets in the UI.\n")
            .append("    Ensure all content is original, engaging, and plagiarism-free. Avoid direct excerpts or copyrighted text.\n")
            .append("    Please provide detailed, rich descriptions for the book titles in this CSV. I am okay with waiting if it requires processing in smaller batches. Please put stronger focus on generating dynamic, context-sensitive content for each book. I am ok it takes longer and  it requires more advanced logic, but I need quality content. Please generate high-quality, dynamic, context-sensitive content for each book.\n")
            .append("Ensure the `Concept` field always includes **exactly 3 distinct points separated by pipes (|)**. This is critical for proper implementation.\n")
            //.append("\nThere are " + RECORD_COUNT + " records in the request you can process them in " + (RECORD_COUNT / BATCH_COUNT) + " batches\n")
            .append("\n");

        // Prompt data

        boolean attachment = false;
        ByteArrayOutputStream outputStream = null;
        PrintWriter writer = null;
        String csvHeader = "bookId,titleSlug,authorId,authorName\n";
        sb.append(csvHeader);
        if(attachment) {
            outputStream = new ByteArrayOutputStream();
            writer = new PrintWriter(outputStream);
            writer.print(csvHeader);
        }

        List<Book> books = Book.getRandomBooksCPT(RECORD_COUNT);
        for (Book book : books) {
            String csvRecord = String.format("%s,%s,%s,%s%n", book.getId(), CommonUtil.slugify(book.getTitle()), book.getAuthor().getId(), book.getAuthor().getName());
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

            parentCategory = saveCategory("Fiction", "fas fa-book-open", null);
            parentCategory.save();
            saveCategory("Romance", "fas fa-heart", parentCategory);
            saveCategory("Mystery", "fas fa-search", parentCategory);
            saveCategory("Fantasy", "fas fa-hat-wizard", parentCategory);
            saveCategory("Science Fiction", "fas fa-rocket", parentCategory);
            saveCategory("Thriller", "fas fa-theater-masks", parentCategory);
            saveCategory("Historical Fiction", "fas fa-landmark", parentCategory);
            saveCategory("Young Adult", "fas fa-user-friends", parentCategory);
            saveCategory("Classics", "fas fa-book", parentCategory);
            saveCategory("Humor", "fas fa-laugh", parentCategory);

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