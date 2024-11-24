package utils;

import models.Book;
import models.GBBook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleBookClient {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    // Later we can use url like this which returns more info: https://www.googleapis.com/books/v1/volumes/tQ1C-rvAfJUC where tQ1C-rvAfJUC is gId

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

                GBBook gbBook = GBBook.findByBookId(book.id);
                if(gbBook == null) {
                    gbBook = new GBBook();

                    // Main fields
                    gbBook.setGbId(jsonObject.optString("id"));
                    gbBook.setEtag(jsonObject.optString("etag"));
                    gbBook.setContentVersion(volumeInfo.optString("contentVersion"));
                    gbBook.setTitle(volumeInfo.optString("title"));
                    gbBook.setSubTitle(volumeInfo.optString("subtitle"));
                    gbBook.setAuthors(convertArrayToCommaSeparatedString(volumeInfo.optJSONArray("authors")));
                    //volumeInfo.getJSONObject("authors").optString("description", "N/A") : "N/A";
                    //map.put("authorDescription", authorDescription);
                    gbBook.setMainCategory(volumeInfo.optString("mainCategory"));
                    gbBook.setCategories(convertArrayToCommaSeparatedString(volumeInfo.optJSONArray("categories")));
                    gbBook.setDescription(volumeInfo.optString("description"));
                    gbBook.setAverageRating(volumeInfo.optDouble("averageRating"));
                    gbBook.setRatingsCount(volumeInfo.optInt("ratingsCount"));
                    gbBook.setMaturityRating(volumeInfo.optString("maturityRating"));

                    // Links
                    gbBook.setSelfLink(jsonObject.optString("selfLink"));
                    gbBook.setPreviewLink(volumeInfo.optString("previewLink"));
                    if (imageLinks != null) {
                        gbBook.setThumbnailUrl(imageLinks.optString("thumbnail"));
                        gbBook.setSmallThumbnailUrl(imageLinks.optString("smallThumbnail"));
                        gbBook.setSmallUrl(imageLinks.optString("small"));
                        gbBook.setMediumUrl(imageLinks.optString("medium"));
                        gbBook.setLargeUrl(imageLinks.optString("large"));
                        gbBook.setExtraLargeUrl(imageLinks.optString("extraLarge"));
                    }
                    gbBook.setInfoLink(volumeInfo.optString("infoLink"));
                    gbBook.setCanonicalVolumeLink(volumeInfo.optString("canonicalVolumeLink"));

                    if (saleInfo != null) {
                        gbBook.setBuyLink(saleInfo.optString("buyLink"));
                        gbBook.setSaleCountry(saleInfo.optString("country"));
                        gbBook.setSaleability(saleInfo.optString("saleability"));
                        gbBook.setEbook(saleInfo.optBoolean("isEbook"));
                    }

                    // Industry identifiers (ISBNs)
                    JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
                    if (industryIdentifiers != null) {
                        for (int i = 0; i < industryIdentifiers.length(); i++) {
                            JSONObject identifier = industryIdentifiers.getJSONObject(i);
                            if ("ISBN_10".equals(identifier.getString("type"))) {
                                gbBook.setIsbn10(identifier.getString("identifier"));
                            } else if ("ISBN_13".equals(identifier.getString("type"))) {
                                gbBook.setIsbn13(identifier.getString("identifier"));
                            }
                        }
                    }

                    // Other info
                    gbBook.setPublisher(volumeInfo.optString("publisher"));
                    gbBook.setPublishedDate(volumeInfo.optString("publishedDate"));
                    gbBook.setPageCount(volumeInfo.optInt("pageCount"));
                    gbBook.setPrintedPageCount(volumeInfo.optInt("printedPageCount"));

                    // Format and access information
                    gbBook.setLanguage(volumeInfo.optString("language"));
                    if (accessInfo != null) {
                        gbBook.setViewability(accessInfo.optString("viewability"));
                        gbBook.setEmbeddable(accessInfo.optBoolean("embeddable"));
                        gbBook.setPublicDomain(accessInfo.optBoolean("publicDomain"));
                        gbBook.setAccessViewStatus(accessInfo.optString("accessViewStatus"));
                        gbBook.setPdfIsAvailable(accessInfo.optJSONObject("pdf").optBoolean("isAvailable"));
                        gbBook.setEpubIsAvailable(accessInfo.optJSONObject("epub").optBoolean("isAvailable"));
                        gbBook.setEpubAcsTokenLink(accessInfo.optJSONObject("epub").optString("acsTokenLink"));
                    }
                    JSONObject readingModes = volumeInfo.optJSONObject("readingModes");
                    if (readingModes != null) {
                        gbBook.setReadingModeText(readingModes.optBoolean("text", false));
                        gbBook.setReadingModeImage(readingModes.optBoolean("image", false));
                    }
                    JSONObject panelizationSummary = volumeInfo.optJSONObject("panelizationSummary");
                    if (panelizationSummary != null) {
                        gbBook.setContainsEpubBubbles(panelizationSummary.optBoolean("containsEpubBubbles", false));
                        gbBook.setContainsImageBubbles(panelizationSummary.optBoolean("containsImageBubbles", false));
                    }

                    gbBook.save();

                    book.setGbBook(gbBook);
                    book.update();

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
