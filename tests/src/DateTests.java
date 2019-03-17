import es.dm.Std;
import es.dm.Date;
import java.time.LocalDate;
import java.time.Instant;

public class DateTests {
  public static void run () {
    System.out.println("Date Tests");

    LocalDate now = Date.now();
    LocalDate aDay = Date.ofStr("20180227");
    assert aDay.toString().equals("2018-02-27");
    assert aDay.compareTo(now) < 0;
    assert Std.dateToStr(aDay).equals("20180227");
    assert Date.toIso(aDay).equals("27/02/2018");
    assert Date.toUs(aDay).equals("02-27-2018");

    assert aDay.equals(Std.dateOfStr("20180227"));
    assert aDay.equals(Date.ofIso("27/02/2018"));
    assert aDay.equals(Date.ofUs("02-27-2018"));

    assert Date.add(aDay, -2).equals(Std.dateOfStr("20180225"));
    assert Date.df(Std.dateOfStr("20180225"), aDay) == -2;

    Instant t1 = Date.tnow();
    Instant t2 = Date.ofMillis(1452563734812L);
    assert t2.compareTo(t1) < 0;
    assert Date.dfMillis(t1, t2) > 0;
    assert Date.dfSeconds(t1, t2) > 0;

    assert Date.dtToStrMillis(t2).equals("12/01/2016 02:55:34,812");
    assert Date.dtToStr(t2).equals("12/01/2016 02:55:34");
    assert Date.tToStrMillis(t2).equals("02:55:34,812");
    assert Date.tToStr(t2).equals("02:55:34");

    Instant t3 = Date.plusMillis(t2, 2000);
    assert t3.equals(Date.plusSeconds(t2, 2));
    assert Date.dfMillis(t3, t2) == 2000;
    assert Date.dfSeconds(t3, t2) == 2;

    System.out.println("    Finished");
  }
}
