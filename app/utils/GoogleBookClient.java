package utils;

import models.Book;
import org.apache.pekko.japi.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleBookClient {
    private static final String API_KEY = "AIzaSyA9Q13ZuoDyitsEFkYnHaBRUDlfhBP9l2o";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    /*private static int getTotalBooksInGenre(String genre) throws Exception {
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
    }*/

    public static Pair<String, String> getCoverImageUrlAndIsbn(String title) {
        try {
            String url = BASE_URL + "?q=intitle:" +  title.replace(" ", "+");
            JSONObject responseJson = getJsonResponse(url);
            JSONArray items = responseJson.optJSONArray("items");
            if(items != null && items.length() > 0) {
                JSONObject jsonObject = items.getJSONObject(0);
                if (jsonObject != null) {
                    JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
                    String isbn = getIsbn(volumeInfo.optJSONArray("industryIdentifiers"));
                    String coverImageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").getString("thumbnail") : "";
                    return  Pair.create(coverImageUrl, isbn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    private static JSONObject getJsonResponse(String urlString) throws Exception {
        StringBuilder content = null;
        int responseCode = 0;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            responseCode = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            if (responseCode == 429) {
                Thread.sleep(10000);
            }
            e.printStackTrace();
        }

        return new JSONObject(content.toString());
    }

}
