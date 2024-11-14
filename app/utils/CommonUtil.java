package utils;

import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
    public static String getVersionedUrl(play.api.mvc.Call url) {
        return getVersionedUrl(url.toString());
    }

    public static String getVersionedUrl(String url) {
        return url + "?v=25";
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
}
