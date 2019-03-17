// Copyright 15-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;

public class Util {
  private Util () {
  }

  // Constants ----------------------------------------------------------------

  /**
   * The sign of euro
   */
  public static final char EURO = '\u20AC';
  /**
   * The sign of euro in HTML format
   */
  public static final String EURO_HTML = "&#8364;";

  // String operations --------------------------------------------------------

  /**
   * <p>Returns one substring of 's'.<p>
   * Includes character 'begin' and excludes character 'end'.<p>
   * If 'begin &lt; 0' or 'end &lt; 0' they become 's.length() + begin'
   * or 's.length() + end'.<p>
   * Next rules are applied, in the following order:
   * <ol>
   *   <li> If 'begin &lt; 0' or 'end &lt; 0' they become 's.length() + begin'
   *     or 's.length()+end'.</li>
   *   <li> If 'begin &lt; 0' it becomes '0'.</li>
   *   <li> If 'end &gt; s.length()' it becomes 's.length()'.</li>
   *   <li> If 'end &lt;= begin' then returns a empty string.</li>
   * </ol>
   *
   * @param s String for extracting a substring.
   * @param begin Position of first character, inclusive.
   * @param end Position of last character, exclusive.
   * @return A substring of 's'
   */
  public static String sub(String s, int begin, int end) {
    int lg = s.length();
    if (begin < 0) {
      begin += lg;
    }
    if (end < 0) {
      end += lg;
    }
    if (begin < 0) {
      begin = 0;
    }
    if (end > lg) {
      end = lg;
    }
    if (end <= begin) {
      return "";
    }
    return s.substring(begin, end);
  }

  /**
   * It Does sub(s, begin, s.length())
   * @param s String for extracting a substring.
   * @param begin Position of first character, inclusive.
   * @return A substring of 's'
   */
  public static String sub(String s, int begin) {
    return sub(s, begin, s.length());
  }

  /**
   * Removes extern spaces and convert duplicate internal spaces to single
   * spaces.
   * @param s String
   * @return New String
   */
  public static String intrim(String s) {
    s = s.trim();
    while (s.contains("  ")) {
      s = s.replace("  ", " ");
    }
    return s;
  }

  /**
   * Returns String resulting of repeating 's' 'times' times.
   * @param s String to replicate
   * @param times Times
   * @return A String
   */
  public static String replicate(String s, int times) {
    StringBuilder r = new StringBuilder();
    for (int i = 0; i < times; i++) {
      r.append(s);
    }
    return r.toString();
  }

  /**
   * Returns String resulting of repeating 'c' 'times' times.
   * @param c Char to replicate
   * @param times Times
   * @return A String
   */
  public static String replicate(char c, int times) {
    char[] chs = new char[times];
    Arrays.fill(chs, c);
    return new String(chs);
  }

  /**
   * Returns string resulting of repeating one space 'times' times.
   * @param times Times
   * @return A String
   */
  public static String replicate(int times) {
    return replicate(' ', times);
  }

  /**
   * Justifies s to left in a width of 'n' characters. If 's.length() &gt; n',
   * trucates it.
   * @param s String to justify
   * @param n width
   * @return A String
   */
  public static String ljust(String s, int n) {
    if (s.length() >= n) {
      return s.substring(0, n);
    }

    return String.format("%-" + n + "s", s);
  }

  /**
   * Justifies s to right in a width of 'n' characters. If 's.length() &gt; n',
   * truncates it.
   * @param s String to justify
   * @param n width
   * @return A String
   */
  public static String rjust(String s, int n) {
    if (s.length() >= n) {
      return s.substring(0, n);
    }

    return String.format("%" + n + "s", s);
  }

  /**
   * Justifies s to center in a width of 'n' characters. If 's.length() &gt; n',
   * truncates it.
   * @param s String to justify
   * @param n width
   * @return A String
   */
  public static String cjust(String s, int n) {
    if (s.length() >= n) {
      return s.substring(0, n);
    }


    int l = (n - s.length()) / 2;
    s = String.format("%-" + l + "s", s);
    return String.format("%" + n + "s", s);
  }

  /**
   * Returns 's' with its first character capitalized
   * @param s A String
   * @return A String
   */
  public static String capitalize(String s) {
    if (s.equals("")) {
      return s;
    }

    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  /**
   * Returns 's' splited and trimed. 'separator' is a regular expresion.
   * @param text String to split
   * @param separator Regular expresion
   * @return A String
   */
  public static String[] splitTrim(String text, String separator) {
    String [] r = text.split(separator);
    for (int i = 0; i < r.length; ++i) {
      r[i] = r[i].trim();
    }
    return r;
  }

  // Others -------------------------------------------------------------------

  /**
   * Returns the 'ClassLoader' of 'es.dm.Util'.
   *
   * @return 'ClassLoader' of 'es.dm.Util'.
   */
  public static ClassLoader classLoader() {
    try {
      return Class.forName("es.dm.Util").getClassLoader();
    } catch (ClassNotFoundException e) {
      System.out.println(e);
      return null;
    }
  }

  /**
   * Returns a resource located in 'dmjava'.
   *
   * @param path Relative to 'dmjava' ClassLoader.
   * @return A resource
   */
  public static InputStream resource(String path) {
    try {
      return classLoader().getResource(path).openStream();
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * Calculates the letter of NIF
   *
   * @param dni Number of document
   * @return Letter
   */
  public static char nif(int dni) {
    String data = "TRWAGMYFPDXBNJZSQVHLCKE";
    return data.charAt(dni % 23);
  }

  /**
   * Equals to numeroAPalabras(n, false)
   *
   * @param n Number
   * @return Spanish spelling of 'n'. Endings in 1 are converted to "un"
   */
  public static String numeroAPalabras(int n) {
    return numeroAPalabras(n, 0);
  }

  /**
   * Converts a integer to word (in Spanish)
   *
   * @param n Number to convert
   * @param female <b>true</b> if number refers a female subject.
   * @return The number converts to words. If number value is 0 return an empty
   * string. Endings in 1 are converted to "uno" if female is false and to "una"
   * if female is true.
   */
  public static String numeroAPalabras(int n, boolean female) {
    if (female) {
      return numeroAPalabras(n, 2);
    } else {
      return numeroAPalabras(n, 1);
    }
  }

  static String numeroAPalabras(int n, int tipo) {
    String p1, p2;

    if (n > 999999) {
      if (n == 1000000) {
        return "un millón";
      } else {
        if (n / 1000000 == 1) {
          p1 = "un millón ";
        } else {
          p1 = numeroAPalabras(n / 1000000, 0);
          p1 = p1 + " millones ";
        }
        p2 = numeroAPalabras(n - n / 1000000 * 1000000, tipo);
        return (p1 + p2).trim();
      }
    } else if (n > 999) {
      if (n / 1000 == 1) {
        p1 = "mil ";
      } else {
        if (tipo == 2) {
          p1 = numeroAPalabras(n / 1000, 2);
        } else {
          p1 = numeroAPalabras(n / 1000, 0);
        }
        p1 = p1 + " mil ";
      }
      p2 = numeroAPalabras(n - n / 1000 * 1000, tipo);
      return (p1 + p2).trim();
    } else if (n > 99) {
      if (n == 100) {
        return "cien";
      } else {
        String terminacion = "os";
        if (tipo == 2) {
          terminacion = "as";
        }
        if (n / 100 == 1) {
          p1 = "ciento ";
        } else if (n / 100 == 5) {
          p1 = "quinient" + terminacion + " ";
        } else if (n / 100 == 7) {
          p1 = "setecient" + terminacion + " ";
        } else if (n / 100 == 9) {
          p1 = "novecient" + terminacion + " ";
        } else {
          p1 = numeroAPalabras(n / 100);
          p1 = p1 + "cient" + terminacion + " ";
        }
        p2 = numeroAPalabras(n - n / 100 * 100, tipo);
        return (p1 + p2).trim();
      }
    } else if (n > 9) {
      if (n == 10) {
        return "diez";
      } else if (n == 11) {
        return "once";
      } else if (n == 12) {
        return "doce";
      } else if (n == 13) {
        return "trece";
      } else if (n == 14) {
        return "catorce";
      } else if (n == 15) {
        return "quince";
      } else if (n == 16) {
        return "dieciseis";
      } else if (n == 17) {
        return "diecisiete";
      } else if (n == 18) {
        return "dieciocho";
      } else if (n == 19) {
        return "diecinueve";
      } else if (n == 20) {
        return "veinte";
      } else {
        if (n / 10 == 2) {
          p1 = "venti";
        } else if (n / 10 == 3) {
          p1 = "treinta";
        } else if (n / 10 == 4) {
          p1 = "cuarenta";
        } else if (n / 10 == 5) {
          p1 = "cincuenta";
        } else if (n / 10 == 6) {
          p1 = "sesenta";
        } else if (n / 10 == 7) {
          p1 = "setenta";
        } else if (n / 10 == 8) {
          p1 = "ochenta";
        } else {
          p1 = "noventa";
        }

        p2 = numeroAPalabras(n - n / 10 * 10, tipo);
        if (!p2.equals("")) {
          if (p1.equals("venti")) {
            return (p1 + p2).trim();
          } else {
            return (p1 + " y " + p2).trim();
          }
        } else {
          return p1;
        }
      }
    } else {
      if (n == 1) {
        if (tipo == 1) {
          return "uno";
        }
        if (tipo == 2) {
          return "una";
        } else {
          return "un";
        }
      } else if (n == 2) {
        return "dos";
      } else if (n == 3) {
        return "tres";
      } else if (n == 4) {
        return "cuatro";
      } else if (n == 5) {
        return "cinco";
      } else if (n == 6) {
        return "seis";
      } else if (n == 7) {
        return "siete";
      } else if (n == 8) {
        return "ocho";
      } else if (n == 9) {
        return "nueve";
      } else {
        return "";
      }
    }
  }

}
