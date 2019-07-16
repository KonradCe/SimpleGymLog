package pl.kcworks.simplegymlog;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.Calendar;

public class DateConverterHelper {

    public static long dateToLong(LocalDate date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
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

    public static int[] gymLogDateFormatToYearMonthDayInt(long gymLogFormatDate) {
        String gymLogDateInString = Long.toString(gymLogFormatDate);
        int year = Integer.parseInt(gymLogDateInString.substring(0, 4));
        int month = Integer.parseInt(gymLogDateInString.substring(4, 6));
        int day = Integer.parseInt(gymLogDateInString.substring(6));

        return new int[]{year, month, day};

    }


}
