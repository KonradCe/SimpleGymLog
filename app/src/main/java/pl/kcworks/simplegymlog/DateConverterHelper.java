package pl.kcworks.simplegymlog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverterHelper {

    public static long dateToLong(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(dateFormat.format(date));
    }

    public static String fromLongToString(long dateInLong) {
        String dateInString = Long.toString(dateInLong);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateInString.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateInString.substring(4, 6)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateInString.substring(6)));

        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    }
}
