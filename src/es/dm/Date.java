// Copyright 17-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.time.LocalDate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Date - Time utilities */
public class Date {

  private Date () {
  }

  /**
   * Returns today LocalDate
   * @return Today LocalDate
   */
  public static LocalDate now () {
    return LocalDate.now();
  }

  /**
   * Returns a LocalDate from a String type "yyyyMMdd"
   * @param d String type "yyyyMMdd"
   * @return LocalDate from String type "yyyyMMdd"
   */
  public static LocalDate ofStr (String d) {
    return LocalDate.parse(d, DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  /**
   * Returns a LocalDate from a String type "dd/MM/yyyy"
   * @param d String type "dd/MM/yyyy"
   * @return LocalDate from String type "dd/MM/yyyy"
   */
  public static LocalDate ofIso (String d) {
    return ofIso(d, '/');
  }

  /**
   * Returns a LocalDate from a String type "dd[sep]MM[sep]yyyy"
   * @param d String type "dd[sep]MM[sep]yyyy"
   * @param separator Separator
   * @return LocalDate from String type "dd[sep]MM[sep]yyyy"
   */
  public static LocalDate ofIso (String d, char separator) {
    return LocalDate.parse(
      d,
      DateTimeFormatter.ofPattern("dd" + separator + "MM" + separator + "yyyy")
    );
  }

  /**
   * Returns a LocalDate from a String type "MM-dd-yyyy"
   * @param d String type "MM-dd-yyyy"
   * @return LocalDate from String type "MM-dd-yyyy"
   */
  public static LocalDate ofUs (String d) {
    return ofUs(d, '-');
  }

  /**
   * Returns a LocalDate from a String type "MM[sep]dd[sep]yyyy"
   * @param d String type "MM[sep]dd[sep]yyyy"
   * @param separator Separator
   * @return LocalDate from String type "MM[sep]dd[sep]yyyy"
   */
  public static LocalDate ofUs (String d, char separator) {
    return LocalDate.parse(
      d,
      DateTimeFormatter.ofPattern("MM" + separator + "dd" + separator + "yyyy")
    );
  }

  /**
   * Returns a LocalDate in format "yyyyMMdd"
   * @param d LocalDate
   * @return LocalDate in format "yyyyMMdd"
   */
  public static String toStr (LocalDate d) {
    return d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  /**
   * Returns a LocalDate in format "dd/MM/yyyy"
   * @param d LocalDate
   * @return LocalDate in format "dd/MM/yyyy"
   */
  public static String toIso (LocalDate d) {
    return toIso(d, '/');
  }

  /**
   * Returns a LocalDate in format "dd[sep]MM[sep]yyyy"
   * @param d LocalDate
   * @param separator Separator
   * @return LocalDate in format "dd[sep]MM[sep]yyyy"
   */
  public static String toIso (LocalDate d, char separator) {
    return d.format(DateTimeFormatter.ofPattern(
      "dd" + separator + "MM" + separator + "yyyy"
    ));
  }

  /**
   * Returns a LocalDate in format "MM-dd-yyyy"
   * @param d LocalDate
   * @return LocalDate in format "MM-dd-yyyy"
   */
  public static String toUs (LocalDate d) {
    return toUs(d, '-');
  }

  /**
   * Returns a LocalDate in format "MM[sep]dd[sep]yyyy"
   * @param d LocalDate
   * @param separator Separator
   * @return LocalDate in format "MM[sep]dd[sep]yyyy"
   */
  public static String toUs (LocalDate d, char separator) {
    return d.format(DateTimeFormatter.ofPattern(
      "MM" + separator + "dd" + separator + "yyyy"
    ));
  }

  /**
   * Returns a new LocalDate adding days (which can be negative)
   * @param d LocalDate
   * @param days Days to add (can be negative)
   * @return A new LocalDate
   */
  public static LocalDate add (LocalDate d, int days) {
    return d.plusDays(days);
  }

  /**
   * Returns difference in days of d1 - d2.
   * @param d1 A LocalDate
   * @param d2 Another LocalDate
   * @return d1 - d2
   */
  public static int df (LocalDate d1, LocalDate d2) {
    return (int) d2.until(d1, ChronoUnit.DAYS);
  }

  /**
   * Return the current time Instant.
   * @return An Instant
   */
  public static Instant tnow () {
    return Instant.now();
  }

  /**
   * Returns an Instant from milliseconds since 1970-01-01.
   * @param millis Milliseconds
   * @return An Instant
   */
  public static Instant ofMillis (long millis) {
    return Instant.ofEpochMilli(millis);
  }

  /**
   * Return an Instant passed to milliseconds since 1970-01-01.
   * @param t An Instant.
   * @return Milliseconds sice 1970-01-01.
   */
  public static long toMillis (Instant t) {
    return t.toEpochMilli();
  }

  /**
   * Adds 'millis' to 't'
   * @param t An Instant
   * @param millis Milliseconds to add
   * @return A new Instant.
   */
  public static Instant plusMillis (Instant t, long millis) {
    return t.plusMillis(millis);
  }

  /**
   * Adds 'seconds' to 't'
   * @param t An Instant
   * @param seconds Seconds to add
   * @return A new Instant.
   */
  public static Instant plusSeconds (Instant t, long seconds) {
    return t.plusSeconds(seconds);
  }

  /**
   * Returns t1 - t2 in milliseconds.
   * @param t1 An Instant
   * @param t2 Another Instant
   * @return t1 - t2
   */
  public static long dfMillis (Instant t1, Instant t2) {
    return t2.until(t1, ChronoUnit.MILLIS);
  }

  /**
   * Returns t1 - t2 in seconds.
   * @param t1 An Instant
   * @param t2 Another Instant
   * @return t1 - t2
   */
  public static long dfSeconds (Instant t1, Instant t2) {
    return t2.until(t1, ChronoUnit.SECONDS);
  }

  /**
   * Returns 't' in format "HH:mm:ss"
   * @param t An Instant
   * @return 't' in format "HH:mm:ss"
   */
  public static String tToStr (Instant t) {
    return LocalDateTime.ofInstant(t, ZoneId.systemDefault()).format(
      DateTimeFormatter.ofPattern("HH:mm:ss")
    );
  }

  /**
   * Returns 't' in format "HH:mm:ss,SSS"
   * @param t An Instant
   * @return 't' in format "HH:mm:ss,SSS"
   */
  public static String tToStrMillis (Instant t) {
    return LocalDateTime.ofInstant(t, ZoneId.systemDefault()).format(
      DateTimeFormatter.ofPattern("HH:mm:ss,SSS")
    );
  }

  /**
   * Returns 't' in format "dd/MM/yyyy HH:mm:ss"
   * @param t An Instant
   * @return 't' in format "dd/MM/yyyy HH:mm:ss"
   */
  public static String dtToStr (Instant t) {
    return LocalDateTime.ofInstant(t, ZoneId.systemDefault()).format(
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    );
  }

  /**
   * Returns 't' in format "dd/MM/yyyy HH:mm:ss,SSS"
   * @param t An Instant
   * @return 't' in format "dd/MM/yyyy HH:mm:ss,SSS"
   */
  public static String dtToStrMillis (Instant t) {
    return LocalDateTime.ofInstant(t, ZoneId.systemDefault()).format(
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss,SSS")
    );
  }
}
