package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GoogleBookClient {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static Map<String, String> getCoverImageUrlAndIsbn(String title) {
        Map<String, String> map = new HashMap();
        try {
            String url = BASE_URL + "?q=intitle:" +  title.replace(" ", "+") + "&key=" + API_KEY;
            JSONObject responseJson = getJsonResponse(url);
            JSONArray items = responseJson.optJSONArray("items");
            if(items != null && items.length() > 0) {
                JSONObject jsonObject = items.getJSONObject(0);
                if (jsonObject != null) {
                    JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
                    String coverImageUrl = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").getString("thumbnail") : "";
                    map.put("coverImageUrl", coverImageUrl);

                    String isbn = getIsbn(volumeInfo.optJSONArray("industryIdentifiers"));
                    map.put("isbn", isbn);

                    String description = volumeInfo.optString("description", "N/A");
                    map.put("description", description);

                    String previewUrl = volumeInfo.optString("previewLink", "N/A");
                    map.put("previewUrl", previewUrl);

                    String authorDescription = volumeInfo.optJSONObject("authors") != null ? volumeInfo.getJSONObject("authors").optString("description", "N/A") : "N/A";
                    map.put("authorDescription", authorDescription);

                    return map;
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
