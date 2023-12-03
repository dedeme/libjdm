// Copyright 16-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.Random;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Arrays;

/** Utilities for program, files and data management. */
public class Std {

  private Std () {
  }

  // Sys ---------------------------------------------------------------------

  static String homev = null;
  static Random random = null;

  /**
   * Initializes application and creates a directory
   * 'home/.dmGroovyApp/$appName'
   * @param appName Application name.
   */
  public static void sysInit (String appName) {
    if (homev != null) {
      throw new IllegalStateException("sysInit already was called");
    }
    homev = fpath(System.getProperty("user.home"), ".dmJavaApp", appName);
    mkdir(homev);
    random = new Random();
  }

  /**
   * Returns the application directory.
   * @return The application directory.
   */
  public static String home () {
    if (homev == null) {
      throw new IllegalStateException("sysInit has not been called");
    }
    return homev;
  }

  /**
   * Execute command 'c' and returns its out + its error outputs.
   * @param cs Command + arguments to execute
   * @return Command output
   */
  public static String cmd (String... cs) {
    try {
      ProcessBuilder pb = new ProcessBuilder(cs);
      pb.redirectErrorStream(true);
      Process p = pb.start();
      p.waitFor();
      BufferedReader rd = new BufferedReader(
        new InputStreamReader(p.getInputStream())
      );
      StringBuilder sb = new StringBuilder();
      String l = rd.readLine();
      while(l != null) {
        sb.append(l);
        sb.append('\n');
        l = rd.readLine();
      }
      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  // File --------------------------------------------------------------------

  /**
   * Makes a path with File.separator
   * @param elements Path elements
   * @return A path
   */
  public static String fpath (String... elements) {
    return String.join(File.separator, elements);
  }

  /**
   * Returns parent directory of file.
   * @param file File
   * @return Parent directory
   */
  public static String fparent (String file) {
    return new File(file).getParent();
  }

  /**
   * Returns file name including extension.
   * @param file File
   * @return File name
   */
  public static String fname (String file) {
    return new File(file).getName();
  }

  /**
   * Returns extension of file including dot, or empty string.
   * @param file File
   * @return Extension
   */
  public static String fextension (String file) {
    String name = fname(file);
    int ix = name.indexOf('.');
    if (ix == -1) {
      return "";
    }
    return name.substring(ix);
  }

  /**
   * Returns name of file without extension, or empty string.
   * @param file File
   * @return Name without extension
   */
  public static String fonlyName (String file) {
    String name = fname(file);
    int ix = name.indexOf('.');
    if (ix == -1) {
      return name;
    }
    return name.substring(0, ix);
  }

  /**
   * Returns the file names of 'path' out of '.' and '..'
   * @param path File path
   * @return File name list
   */
  public static String[] dir (String path) {
    return new File(path).list();
  }

  /**
   * Returns true if 'path' exists in the file system.
   * @param path Path
   * @return 'true' if path exists
   */
  public static boolean fexists (String path) {
    return new File(path).exists();
  }

  /**
   * Returns true if 'path' is a directory.
   * @param path Path
   * @return 'true' if path is a directory
   */
  public static boolean isDirectory (String path) {
    return new File(path).isDirectory();
  }

  /**
   * Returns length of 'path'
   * @param path Path
   * @return File length
   */
  public static long flen (String path) {
    return new File(path).length();
  }

  /**
   * Returns last modification date in milliseconds since 01-01-70.
   * @param path Path
   * @return Data of last modification of path
   */
  public static long fmodified (String path) {
    return new File(path).lastModified();
  }

  /**
   * Creates a directory and all its parents if it does not exist.
   * @param path Path
   */
  public static void mkdir (String path) {
    new File(path).mkdirs();
  }

  /**
   * Creates a new temporary file and returns its path.
   * @return File name
   */
  public static String tmpf () {
    try {
      return File.createTempFile("dmJava", "").toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "tmpFailed";
    }
  }

  /**
   * Creates a new temporary directory and returns its path.
   * @return File name
   */
  public static String tmpd () {
    try {
      return java.nio.file.Files.createTempDirectory("dmJava").toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "tmpFailed";
    }
  }

  /**
   * Removes 'path' for the file system, although it is a non empty directory.
   * @param path Path
   */
  public static void del (String path) {
    if (isDirectory(path)) {
      for (String f : dir(path)) {
        del(fpath(path, f));
      }
    }
    new File(path).delete();
  }

  /**
   * Changes the name of 'source' to 'target'. If it fails, returns 'false'.
   * @param source Source
   * @param target Target
   * @return 'false' if operation fails.
   */
  public static boolean rename (String source, String target) {
    return new File(source).renameTo(new File(target));
  }

  /**
   * Copies 'source' to 'target'. If it fails, returns 'false'.
   * @param source Source
   * @param target Target
   * @return 'false' if operation fails.
   */
  public static boolean copy (String source, String target) {
    try {
      Files.copy(
        new File(source).toPath(),
        new File(target).toPath(),
        StandardCopyOption.COPY_ATTRIBUTES,
        StandardCopyOption.REPLACE_EXISTING
      );
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Compresses source to target using a buffer of 4096 bytes, UTF-8 as
   * default charset and compressing all files.
   *
   * @param source Can be a file or a directory.
   * @param target Complete name of file .zip. (e.g. "com.zip")
   * @return 'false' if zipping fails
   */
  public static boolean zip (String source, String target) {
    try {
      Zip.zip (new File(source), new File(target));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Unzip source in the 'target' directory, using a buffer of 4096 bytes and
   * encoded with UTF-8.
   *
   * @param source Zip file with complete name.
   * @param target Directory to uncompress.
   * @return 'false' if unzipping fails
   */
  public static boolean unzip (String source, String target) {
    try {
      Zip.unzip (new File(source), new File(target));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Writes 'text' in path as a utf-8 string.<p>
   * <i>NOTE: Writting remove the last '\n' of 'text' if it exists.</i>
   * @param path Path
   * @param text Text
   */
  public static void write (String path, String text) {
    try {
      new FileOutputStream(path).write(text.getBytes("UTF-8"));
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends 'text' in path as a utf-8 string.<p>
   * <i>NOTE: Writting remove the last '\n' of 'text' if it exists.</i>
   * @param path Path
   * @param text Text
   */
  public static void append (String path, String text) {
    try {
      new FileOutputStream(path, true).write(text.getBytes("UTF-8"));
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Iterates through a file line by line as a utf-8 text.
   * @param path File path
   * @param closure A closure
   */
  public static void eachLine(String path, Consumer<String> closure) {
    try {
      BufferedReader d = new BufferedReader(new InputStreamReader(
        new FileInputStream(path), "UTF-8"
      ));
      d.lines().forEach(closure);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads all text from 'path' as a utf-8 string.
   * @param path Path
   * @return Lines of path
   */
  public static List<String> readLines (String path) {
    try {
      BufferedReader d = new BufferedReader(new InputStreamReader(
        new FileInputStream(path), "UTF-8"
      ));
      return d.lines().collect(Collectors.toList());
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Reads all text from 'path' as a utf-8 string.
   * @param path Path
   * @return Texto of path
   */
  public static String read (String path) {
    return String.join("\n", readLines(path));
  }

  // Date - Time --------------------------------------------------------------

  /**
   * Returns today LocalDate
   * @return Today LocalDate
   */
  public static LocalDate now() {
    return LocalDate.now();
  }

  /**
   * Returns a LocalDate from a String type "yyyyMMdd"
   * @param d String type "yyyyMMdd"
   * @return LocalDate from String type "yyyyMMdd"
   */
  public static LocalDate dateOfStr (String d) {
    return Date.ofStr(d);
  }

  /**
   * Returns a LocalDate in format "yyyyMMdd"
   * @param d LocalDate
   * @return LocalDate in format "yyyyMMdd"
   */
  public static String dateToStr (LocalDate d) {
    return Date.toStr(d);
  }

  /**
   * Returns milliseconds since 1970-01-01.
   * @return Milliseconds
   */
  public static long tnow() {
    return Date.toMillis(Date.tnow());
  }

  // Numbers  -----------------------------------------------------------------

  /**
   * Fix 'n' with 'dec' decimals.
   * @param n A number
   * @param dec Number of decimal positions (must be positive or 0)
   * @return 'n' with 'dec' decimals.
   */
  public static double fix (double n, int dec) {
    double pow = Math.pow(dec, 10);
    return Math.round(n * pow) / pow;
  }

  /**
   * Returns a String type "nnnn.nnn"
   * @param n A number
   * @param dec Number of decimal positions (must be positive or 0)
   * @return String type "nnnn.nnn".
   */
  public static String nToStr (double n, int dec) {
    if (dec == 0) {
      return new DecimalFormat(".", new DecimalFormatSymbols(Locale.US))
        .format(n);
    } else {
      String dc = "";
      for (int i = 0; i < dec; ++i) {
        dc += "0";
      }
      return new DecimalFormat(
        "." + dc,
        new DecimalFormatSymbols(Locale.US)
      ).format(n);
    }
  }

  /**
   * Returns a number from a String in format "nnnn.nnn".
   * @param n A string containing a number.
   * @return A number.
   */
  public static double nOfStr (String n) {
    return Double.parseDouble(n);
  }

  /**
   * Returns 'true' if n1 == n2 with a precision of 0.1
   * @param n1 A number
   * @param n2 Another number
   * @return 'true' if n1 == n2 with a precision of 0.1
   */
  public static boolean eq0 (double n1, double n2) {
    return eqN(n1, n2, 1);
  }

  /**
   * Returns 'true' if n1 == n2 with a precision of 0.001
   * @param n1 A number
   * @param n2 Another number
   * @return 'true' if n1 == n2 with a precision of 0.001
   */
  public static boolean eq2 (double n1, double n2) {
    return eqN(n1, n2, 3);
  }

  /**
   * Returns 'true' if n1 == n2 with a precision of 'gap'
   * @param n1 A number
   * @param n2 Another number
   * @param gap Precision of comparation (must be positive)
   * @return 'true' if n1 == n2 with a precision of 'gap'
   */
  public static boolean eqN (double n1, double n2, double gap) {
    return n2 < n1 + gap && n2 > n1 - gap;
  }


  static String nTo (double n, int dec, Locale lc) {
    String tp1 = "###,###,###,###,###";
    if (dec == 0) {
      return new DecimalFormat(tp1, new DecimalFormatSymbols(lc))
        .format(n);
    } else {
      String dc = "";
      for (int i = 0; i < dec; ++i) {
        dc += "0";
      }
      return new DecimalFormat(
        tp1 + "." + dc,
        new DecimalFormatSymbols(lc)
      ).format(n);
    }
  }

  /**
   * Returns a String type "n.nnn,nn"
   * @param n A number
   * @param dec Number of decimal positions (must be positive or 0)
   * @return String type "n.nnn,nn".
   */
  public static String nToIso (double n, int dec) {
    return nTo(n, dec, Locale.ITALY);
  }

  /**
   * Returns a number from a String in format "n.nnn,nn".
   * @param n A string containing a number.
   * @return A number.
   */
  public static double nOfIso (String n) {
    return nOfStr(n.replace(".", "").replace(',', '.'));
  }

  /**
   * Returns a String type "n,nnn.nn"
   * @param n A number
   * @param dec Number of decimal positions (must be positive or 0)
   * @return String type "n,nnn.nn".
   */
  public static String nToUs (double n, int dec) {
    return nTo(n, dec, Locale.US);
  }

  /**
   * Returns a number from a String in format "n,nnn.nn".
   * @param n A string containing a number.
   * @return A number.
   */
  public static double nOfUs (String n) {
    return nOfStr(n.replace(",", ""));
  }

  /**
   * Returns a random number between 0 (inclusive) and 1 (exclusive).
   * @return Random number between 0 (inclusive) and 1 (exclusive).
   */
  public static double rnd () {
    return random.nextDouble();
  }

  /**
   * Returns a random number between 0 (inclusive) and 'bound' (exclusive).
   * @param bound Limit for generator (must be positive)
   * @return Random number between 0 (inclusive) and 'bound' (exclusive).
   */
  public static int rndi (int bound) {
    return random.nextInt(bound);
  }

  // Others -------------------------------------------------------------------

  /**
   * Sorts 'l' randomly.
   * @param l List to sort.
   */
  public static <T> void shuffle (List<T> l) {
    for (int ix = l.size(); ix > 1; --ix) {
      int rnd = rndi(ix);
      T tmp = l.get(rnd);
      l.set(rnd, l.get(ix - 1));
      l.set(ix - 1, tmp);
    }
  }

  /**
   * Return a Stream from an Iterable.
   * @param <T> Generic
   * @param it Iterable
   * @return A Stream
   */
  public static <T> Stream<T> toStream (Iterable<T> it) {
    return StreamSupport.stream(it.spliterator(), false);
  }

  /**
   * Returns message and stackTrace of an Exception.
   * @param e Exception
   * @return Message + stackTrace
   */
  public static String stackTrace(Exception e) {
    return e.toString() + "\n" +
      String.join("\n", Arrays.asList(e.getStackTrace()).stream().map(
        el -> el.toString()
      ).collect(Collectors.toList()))
    ;
  }

  public static String http (
    String url
  ) throws MalformedURLException, IOException {
    BufferedReader conn = new BufferedReader(
      new InputStreamReader(new URL(url).openStream())
    );

    StringBuilder tx = new StringBuilder();
    String l = conn.readLine();
    while (l != null) {
      tx.append(l);
      tx.append("\n");
      l = conn.readLine();
    }

    return tx.toString();
  }

}
