package utils;

import models.Book;
import models.GoogleBook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleBookClientV2 {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    // Later we can use url like this which returns more info: https://www.googleapis.com/books/v1/volumes/tQ1C-rvAfJUC where tQ1C-rvAfJUC is gId

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

    public static void importGoogleBookInfo(Book book) {
        try {
            String url = BASE_URL + "?q=intitle:" +  book.title.replace(" ", "+") + "&key=" + API_KEY;
            JSONObject responseJson = getJsonResponse(url);
            JSONArray items = responseJson.optJSONArray("items");
            JSONObject jsonObject = null;
            JSONObject volumeInfo = null;
            JSONObject saleInfo = null;
            JSONObject accessInfo = null;
            JSONObject imageLinks = null;
            if(items != null && items.length() > 0) {
                jsonObject = items.getJSONObject(0);
                if (jsonObject != null) {
                    volumeInfo = jsonObject.getJSONObject("volumeInfo");
                    saleInfo = jsonObject.optJSONObject("saleInfo");
                    accessInfo = jsonObject.optJSONObject("accessInfo");
                    imageLinks = volumeInfo.optJSONObject("imageLinks");
                }

                GoogleBook googleBook = GoogleBook.findByBookId(book.id);
                if(googleBook == null) {
                    googleBook = new GoogleBook();
                    googleBook.setBook(book);

                    // Main fields
                    googleBook.setGbId(jsonObject.optString("id"));
                    googleBook.setEtag(jsonObject.optString("etag"));
                    googleBook.setContentVersion(volumeInfo.optString("contentVersion"));
                    googleBook.setTitle(volumeInfo.optString("title"));
                    googleBook.setSubTitle(volumeInfo.optString("subtitle"));
                    googleBook.setAuthors(convertArrayToCommaSeparatedString(volumeInfo.optJSONArray("authors")));
                    googleBook.setMainCategory(volumeInfo.optString("mainCategory"));
                    googleBook.setCategories(convertArrayToCommaSeparatedString(volumeInfo.optJSONArray("categories")));
                    googleBook.setDescription(volumeInfo.optString("description"));
                    googleBook.setAverageRating(volumeInfo.optDouble("averageRating"));
                    googleBook.setRatingsCount(volumeInfo.optInt("ratingsCount"));
                    googleBook.setMaturityRating(volumeInfo.optString("maturityRating"));

                    // Links
                    googleBook.setSelfLink(jsonObject.optString("selfLink"));
                    googleBook.setPreviewLink(volumeInfo.optString("previewLink"));
                    if (imageLinks != null) {
                        googleBook.setThumbnailUrl(imageLinks.optString("thumbnail"));
                        googleBook.setSmallThumbnailUrl(imageLinks.optString("smallThumbnail"));
                        googleBook.setSmallUrl(imageLinks.optString("small"));
                        googleBook.setMediumUrl(imageLinks.optString("medium"));
                        googleBook.setLargeUrl(imageLinks.optString("large"));
                        googleBook.setExtraLargeUrl(imageLinks.optString("extraLarge"));
                    }
                    googleBook.setInfoLink(volumeInfo.optString("infoLink"));
                    googleBook.setCanonicalVolumeLink(volumeInfo.optString("canonicalVolumeLink"));

                    if (saleInfo != null) {
                        googleBook.setBuyLink(saleInfo.optString("buyLink"));
                        googleBook.setSaleCountry(saleInfo.optString("country"));
                        googleBook.setSaleability(saleInfo.optString("saleability"));
                        googleBook.setEbook(saleInfo.optBoolean("isEbook"));
                    }

                    // Industry identifiers (ISBNs)
                    JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
                    if (industryIdentifiers != null) {
                        for (int i = 0; i < industryIdentifiers.length(); i++) {
                            JSONObject identifier = industryIdentifiers.getJSONObject(i);
                            if ("ISBN_10".equals(identifier.getString("type"))) {
                                googleBook.setIsbn10(identifier.getString("identifier"));
                            } else if ("ISBN_13".equals(identifier.getString("type"))) {
                                googleBook.setIsbn13(identifier.getString("identifier"));
                            }
                        }
                    }

                    // Other info
                    googleBook.setPublisher(volumeInfo.optString("publisher"));
                    googleBook.setPublishedDate(volumeInfo.optString("publishedDate"));
                    googleBook.setPageCount(volumeInfo.optInt("pageCount"));
                    googleBook.setPrintedPageCount(volumeInfo.optInt("printedPageCount"));

                    // Format and access information
                    googleBook.setLanguage(volumeInfo.optString("language"));
                    if (accessInfo != null) {
                        googleBook.setViewability(accessInfo.optString("viewability"));
                        googleBook.setEmbeddable(accessInfo.optBoolean("embeddable"));
                        googleBook.setPublicDomain(accessInfo.optBoolean("publicDomain"));
                        googleBook.setAccessViewStatus(accessInfo.optString("accessViewStatus"));
                        googleBook.setPdfIsAvailable(accessInfo.optJSONObject("pdf").optBoolean("isAvailable"));
                        googleBook.setEpubIsAvailable(accessInfo.optJSONObject("epub").optBoolean("isAvailable"));
                        googleBook.setEpubAcsTokenLink(accessInfo.optJSONObject("epub").optString("acsTokenLink"));
                    }
                    JSONObject readingModes = volumeInfo.optJSONObject("readingModes");
                    if (readingModes != null) {
                        googleBook.setReadingModeText(readingModes.optBoolean("text", false));
                        googleBook.setReadingModeImage(readingModes.optBoolean("image", false));
                    }
                    JSONObject panelizationSummary = volumeInfo.optJSONObject("panelizationSummary");
                    if (panelizationSummary != null) {
                        googleBook.setContainsEpubBubbles(panelizationSummary.optBoolean("containsEpubBubbles", false));
                        googleBook.setContainsImageBubbles(panelizationSummary.optBoolean("containsImageBubbles", false));
                    }

                    googleBook.save();

                    System.out.println("Processed: " + book.title);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static String convertArrayToCommaSeparatedString(JSONArray jsonArray) {
        if (jsonArray == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            result.append(jsonArray.getString(i));
            if (i < jsonArray.length() - 1) result.append(", ");
        }
        return result.toString();
    }

}
