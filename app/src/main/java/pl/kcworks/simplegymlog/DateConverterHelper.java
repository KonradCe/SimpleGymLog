package pl.kcworks.simplegymlog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DateConverterHelper {

    public static long dateToLong(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(dateFormat.format(date));
    }

    public static long dateToLongMonthOnly(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
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

    public static Set<Long> convertListOfGymLogDateFormatToSetOfTimeInMillis (List<Long> datesInGymLogDateFormat) {
        Set<Long> setOfDatesInMillis = new HashSet<>();
        Calendar calendar = Calendar.getInstance();

        for (Long dateInGymLogFormat : datesInGymLogDateFormat) {
            String dateInString = dateInGymLogFormat.toString();
            calendar.set(Calendar.YEAR, Integer.parseInt(dateInString.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateInString.substring(4, 6)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateInString.substring(6)));

            setOfDatesInMillis.add(calendar.getTimeInMillis());
        }

        return setOfDatesInMillis;
    }

}
