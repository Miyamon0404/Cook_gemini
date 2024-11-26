package cook;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static int differenceDays(String strDate1, String strDate2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate date1 = LocalDate.parse(strDate1, formatter);
        LocalDate date2 = LocalDate.parse(strDate2, formatter);
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }
}




/*
git remote set-url origin https://Naoya-lg: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@github.com/Miyamon0404/Cook.git

例:

*/