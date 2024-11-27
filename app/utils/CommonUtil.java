package utils;

import com.github.slugify.Slugify;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;

import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
    public static String getVersionedUrl(play.api.mvc.Call url) {
        return getVersionedUrl(url.toString());
    }

    public static String getVersionedUrl(String url) {
        return url + "?v=32";
    }

    // Session related

    public static User getUser(Http.Request request) {
        String userId = getUserId(request);
        return User.find(userId);
    }

    public static String getUserId(Http.Request request) {
        return request.session().get("userId").get();
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

    // Other

    public static String slugify(String inputString) {
        String slug = "";
        if(StringUtils.isNotBlank(inputString)) {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(inputString);
        }
        return slug;
    }
}
