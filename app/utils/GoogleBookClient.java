package utils;

import models.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoogleBookClient {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    private static final String[] CATEGORY_MIND_AND_SPIRIT = {"Psychology", "Philosophy", "Spirituality"};
    private static final String[] CATEGORY_PERSONAL_GROWTH = {"Self-Help"/**, "Health"**/};
    private static final String[] CATEGORY_BUSINESS_LEADERSHIP = {"Business", "Leadership", "Economics"};
    private static final String[] CATEGORY_BIOGRAPHY_AND_MEMOIRS = {"Biography"};
    private static final String[] CATEGORY_CHILDREN_BOOKS = {"Children's"};

    public static List<Book> generateRandomBooks() throws Exception {
        List<Book> books = new ArrayList<>();
        books.add(generateRandomBook(CATEGORY_MIND_AND_SPIRIT));
        books.add(generateRandomBook(CATEGORY_PERSONAL_GROWTH));
        books.add(generateRandomBook(CATEGORY_BUSINESS_LEADERSHIP));
        books.add(generateRandomBook(CATEGORY_BIOGRAPHY_AND_MEMOIRS));
        books.add(generateRandomBook(CATEGORY_CHILDREN_BOOKS));
        return books;
    }

    public static Book generateRandomBook(String[] category) throws Exception {
        Random random = new Random();
        String randomGenre = category[random.nextInt(category.length)];
        int totalBooks = getTotalBooksInGenre(randomGenre);
        int randomIndex = new Random().nextInt(totalBooks);
        JSONObject jsonObject = fetchBookAtIndex(randomGenre, randomIndex);
        Book book = new Book();
        if (jsonObject != null) {
            JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
            String isbn = getIsbn(volumeInfo.optJSONArray("industryIdentifiers"));
            book.title = volumeInfo.optString("title", "No title available");

            System.out.println("Index: " + randomIndex + ", Title: " + book.title);

            //book.genre = randomGenre;
            //book.youtubeUrl = generateYouTubeUrl(book.title);
            if(isbn != null && isbn.trim().length() > 0 && !isbn.equals("No ISBN available")) {
                //book.amazonUrl = generateAmazonUrlFromIsbn(isbn);
                book.goodReadsUrl = generateGoodreadsUrlFromIsbn(isbn);
                //book.googleBooksUrl = generateGoogleBooksUrlFromIsbn(isbn);
                //book.blinkistUrl = generateBlinkistUrlFromIsbn(isbn);
            } else {
                //book.amazonUrl = generateAmazonUrl(book.title);
                book.goodReadsUrl = generateGoodreadsUrl(book.title);
                //book.googleBooksUrl = generateGoogleBooksUrl(book.title);
                //book.blinkistUrl = generateBlinkistUrl(book.title);
            }
        }
        return book;
    }

    private static int getTotalBooksInGenre(String genre) throws Exception {
        //String url = String.format("%s?q=subject:%s&printType=books&maxResults=1&key=%s", BASE_URL, genre, API_KEY);
        //String url = BASE_URL + "?q=subject:" + genre + "&key=" + API_KEY;
        String url = BASE_URL + "?q=subject:" + genre + "&langRestrict=en&printType=books&maxResults=1&orderBy=relevance&key=" + API_KEY;
        JSONObject responseJson = getJsonResponse(url);
        int totalBooks = responseJson.optInt("totalItems", 0);
        System.out.println("Genre: " + genre + ", Total books: " + totalBooks);
        return totalBooks;
    }

    private static JSONObject fetchBookAtIndex(String genre, int index) throws Exception {
        int MAX_RESULTS = 40;
        String url = BASE_URL + "?q=subject:" + genre + "&langRestrict=en&printType=books&maxResults=" + MAX_RESULTS + "&startIndex=" + index + "&orderBy=relevance&key=" + API_KEY;
        //String url = String.format("%s?q=subject:%s&printType=books&startIndex=%d&maxResults=1&key=%s", BASE_URL, genre, index, API_KEY);
        JSONObject responseJson = getJsonResponse(url);
        JSONArray items = responseJson.optJSONArray("items");
        if(items != null && items.length() > 0) {
            int randomIndex = new Random().nextInt(items.length());
            return items.getJSONObject(randomIndex);
        }
        return null;
    }

    private static String getIsbn(JSONArray identifiers) {
        if (identifiers != null) {
            for (int i = 0; i < identifiers.length(); i++) {
                JSONObject identifier = identifiers.getJSONObject(i);
                if (identifier.getString("type").equals("ISBN_13")) {
                    return identifier.getString("identifier");
                }
            }
        }
        return "No ISBN available";
    }

    private static String generateAmazonUrl(String title) {
        // Replace spaces in the title with '+' and append it to the Amazon URL structure
        return "https://www.amazon.com/s?k=" + title.replace(" ", "+");
    }

    private static String generateAmazonUrlFromIsbn(String isbn) {
        // Construct the Amazon URL using the ISBN
        return "https://www.amazon.com/s?k=" + isbn;
    }

    private static String generateGoodreadsUrl(String title) {
        // Replace spaces in the title with '+' and append it to the Goodreads URL structure
        return "https://www.goodreads.com/search?q=" + title.replace(" ", "+");
    }

    private static String generateGoodreadsUrlFromIsbn(String isbn) {
        // Construct the Goodreads URL using the ISBN
        return "https://www.goodreads.com/search?q=" + isbn;
    }

    private static String generateYouTubeUrl(String title) {
        // Replace spaces in the title with '+' and append it to the YouTube search URL structure
        return "https://www.youtube.com/results?search_query=" + title.replace(" ", "+") + "+book+summary";
    }

    private static String generateGoogleBooksUrl(String title) {
        // Replace spaces in the title with '+' and append it to the Google Books URL structure
        return "https://books.google.com/books?id=" + title.replace(" ", "+");
    }

    private static String generateGoogleBooksUrlFromIsbn(String isbn) {
        // Append ISBN to the Google Books URL structure
        return "https://books.google.com/books?vid=ISBN" + isbn;
    }

    private static String generateBlinkistUrl(String title) {
        // Replace spaces in the title with '-' and append it to the Blinkist URL structure
        return "https://www.blinkist.com/en/books/" + title.replace(" ", "-").toLowerCase();
    }

    private static String generateBlinkistUrlFromIsbn(String isbn) {
        // Append ISBN to the Blinkist URL structure
        return "https://www.blinkist.com/en/books/" + isbn;
    }

    private static JSONObject getJsonResponse(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return new JSONObject(content.toString());
    }

}
