package utils;

import com.github.slugify.Slugify;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonUtil {
    // Constants

    public static final int DISCARD_QUOTE_CHAR_LIMIT = 200;

    public static String getVersionedUrl(play.api.mvc.Call url) {
        return getVersionedUrl(url.toString());
    }

    public static String getVersionedUrl(String url) {
        return url + "?v=44";
    }

    // Request related

    public static String getRequestBodyParam(Http.Request request, String key) {
        String[] valuesArray = request.body().asFormUrlEncoded().get(key);
        return (valuesArray != null && valuesArray.length > 0) ? valuesArray[0] : null;
    }

    // Session related

    public static User getUser(Http.Request request) {
        String userId = getUserId(request);
        return userId != null ? User.find(userId) : null;
    }

    public static String getUserId(Http.Request request) {
        return request.session().get("userId").orElse(null);
    }

    // Date related

    public static Date incrementDateByDays(Date date, int numberOfDays) {
        date = removeTimeStamp(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, numberOfDays);
        return calendar.getTime();
    }

    public static Date removeTimeStamp(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // String related

    public static String slugify(String str) {
        String slug = "";
        if(StringUtils.isNotBlank(str)) {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(str);
        }
        return slug;
    }

    public static List<String> splitToList(String str) {
        return Arrays.asList(str.split("\\|"));
    }
}
