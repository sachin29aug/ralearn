package controllers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.mvc.Controller;
import play.mvc.Result;

public class ContentAdmin extends Controller {

    public Result scrapBlinkCategories() {
        try {
            Document doc = Jsoup.connect("https://www.blinkist.com")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();
            Elements elements = doc.select("div.flex.w-full.items-center.font-medium");
            for (Element element : elements) {
                String value = element.text();
                System.out.println(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok("");
    }
}
