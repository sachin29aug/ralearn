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

    public static GBBook importGoogleBookInfo(Book book) {
        GBBook gbBook = null;
        try {
            String url = BASE_URL + "?q=intitle:" +  book.getTitle().replace(" ", "+") + "+inauthor:" + book.getAuthor().getName().replace(" ", "+") + "&key=" + API_KEY;
            System.out.println(url);
            JSONObject responseJson = getJsonResponse(url);
            JSONObject jsonResponseItem = getBestJsonResponseItem(responseJson);
            if(jsonResponseItem != null) {
                JSONObject volumeInfo = jsonResponseItem.getJSONObject("volumeInfo");
                JSONObject saleInfo = jsonResponseItem.optJSONObject("saleInfo");
                JSONObject accessInfo = jsonResponseItem.optJSONObject("accessInfo");
                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");

                gbBook = GBBook.findByBookId(book.id);
                if(gbBook == null) {
                    gbBook = new GBBook();

                    // Main fields
                    gbBook.setGbId(jsonResponseItem.optString("id"));
                    gbBook.setEtag(jsonResponseItem.optString("etag"));
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
                    gbBook.setSelfLink(jsonResponseItem.optString("selfLink"));
                    String previewLink = volumeInfo.optString("previewLink");
                    gbBook.setPreviewLink(previewLink != null ? previewLink.replace("http://", "https://") : null);
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

                    System.out.println("Processed: " + book.getTitle());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gbBook;
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

    private static JSONObject getBestJsonResponseItem(JSONObject responseJson) {
        JSONArray items = responseJson.optJSONArray("items");
        JSONObject bestItem = null;
        if(items != null && items.length() > 0) {
            bestItem = items.optJSONObject(0);
            int highestRatingsCount = 0;
            double highestAverageRating = 0;
            String latestPublishedDate = "1900-01-01";
            for (int i = 0; i < items.length(); i++) {
                JSONObject currentItem = items.optJSONObject(i);
                JSONObject volumeInfo = currentItem.optJSONObject("volumeInfo");
                if (volumeInfo == null || volumeInfo.optJSONObject("imageLinks") == null) {
                    continue;
                }
                int currentRatingsCount = volumeInfo.optInt("ratingsCount", 0);
                double currentAverageRating = volumeInfo.optDouble("averageRating", 0);
                String currentPublishedDate = volumeInfo.optString("publishedDate", "1900-01-01");
                if (currentRatingsCount > highestRatingsCount) {
                    bestItem = currentItem;
                    highestRatingsCount = currentRatingsCount;
                    highestAverageRating = currentAverageRating;
                    latestPublishedDate = currentPublishedDate;
                } else if (currentRatingsCount == highestRatingsCount) {
                    if (currentAverageRating > highestAverageRating) {
                        bestItem = currentItem;
                        highestAverageRating = currentAverageRating;
                        latestPublishedDate = currentPublishedDate;
                    } else if (currentAverageRating == highestAverageRating) {
                        if (currentPublishedDate.compareTo(latestPublishedDate) > 0) {
                            bestItem = currentItem;
                            latestPublishedDate = currentPublishedDate;
                        }
                    }
                }
            }
        }
        return bestItem;
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
