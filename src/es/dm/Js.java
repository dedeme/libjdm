// Copyright 15-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Json encoder and decoder */
public class Js {
  String tx = "";
  int ix = 0;
  int len = 0;

  /**
   * @param tx It must be a valid JSON string.
   */
  public Js(String tx) {
    this.tx = tx;
    len = tx.length();
  }

  void exc(String msg) {
    int rest = tx.length() - ix;
    throw new IllegalArgumentException(
      msg + "\n" +
      (ix < 30 ? tx.substring(0, ix) : "..." + tx.substring(ix - 30, ix)) +
      "_|_" +
      (rest < 30 ? tx.substring(ix) : tx.substring(ix, ix + 30) + "...")
    );
  }

  void excSpare (String value) {
    exc("Spare characters reading a " + value + " value");
  }

  boolean starts (String s) {
    return tx.substring(ix).startsWith(s);
  }

  void blanks () {
    while (ix < len && tx.charAt(ix) <= ' ') {
      ++ix;
    }
  }

  char rnext () {
    ++ix;
    if (ix < len) {
      return tx.charAt(ix);
    }
    return '\0';
  }

  boolean rend () {
    blanks();
    return ix == len;
  }

  // tx[ix] results as ',', ']', '}' or ix = len
  void skipValue () {
    if (ix < len) {
      char ch = tx.charAt(ix);
      while (ch != ',' && ch != ']' && ch != '}' && ch != '\0')
        ch = rnext();
    }
  }

  // Starts at '"' and end after '"' + BLANKS
  void skipString () {
    char ch;
    for (;;) {
      ch = rnext();
      if (ch == '"') {
        ++ix;
        blanks();
        break;
      }
      if (ch == '\\') {
        ch = rnext();
        if (ch == 'u') {
          rnext();
          rnext();
          rnext();
          rnext();
        }
      }
    }
  }

  // Starts at '[' and end after ']' + BLANKS
  void skipArray () {
    ++ix;
    blanks();
    if (ix == len) {
      exc("Expected ']' skiping array");
    }

    char ch = tx.charAt(ix);
    if (ch == ']') {
      ++ix;
      blanks();
      return;
    }

    for (;;) {
      if (ch == '"') {
        skipString();
      } else if (ch == '[') {
        skipArray();
      } else if (ch == '{') {
        skipObject();
      } else {
        skipValue();
      }

      if (ix == len) {
        exc("Expected ']' skiping array");
      }
      ch = tx.charAt(ix);

      if (ch == ',') {
        ++ix;
        blanks();
        if (ix == len) {
          exc("Expected ']' skiping array");
        }
        ch = tx.charAt(ix);
        continue;
      }
      if (ch == ']') {
        ++ix;
        blanks();
        break;
      }

      exc("Expected ',' or ']' skiping array");
    }
  }

  // Starts at '{' and end after '}' + BLANKS
  void skipObject () {
    ++ix;
    blanks();
    if (ix == len) {
      exc("Expected '}' skiping object");
    }

    char ch = tx.charAt(ix);
    if (ch == '}') {
      ++ix;
      blanks();
      return;
    }

    for (;;) {
      if (ch != '"') {
        exc("Expected '\"' skiping object key");
      }
      skipString();

      if (ix == len) {
        exc("Expected ':' skiping object");
      }
      ch = tx.charAt(ix);
      if (ch != ':') {
        exc("Expected ':' skiping object");
      }
      ++ix;
      blanks();

      if (ix == len) {
        exc("Expected '}' skiping object");
      }
      ch = tx.charAt(ix);
      if (ch == '"') {
        skipString();
      } else if (ch == '[') {
        skipArray();
      } else {
        skipValue();
      }

      if (ix == len) {
        exc("Expected '}' skiping object");
      }
      ch = tx.charAt(ix);
      if (ch == ',') {
        ++ix;
        blanks();
        if (ix == len) {
          exc("Expected '}' skiping object");
        }
        ch = tx.charAt(ix);
        continue;
      }
      if (ch == '}') {
        ++ix;
        blanks();
        break;
      }

      exc("Expected ',' or '}' skiping object");
    }
  }

  /**
   * Reads a null value.
   * @return true if js is equals to "null"
   */
  public boolean isNull () {
    blanks();
    if (starts("null")) {
      ix += 4;
      return rend();
    }
    return false;
  }

  /**
   * Reads a boolean value.
   * @return The boolean value of 'js'
   */
  public boolean rBoolean () {
    blanks();
    boolean r = true;
    if (starts("true")) {
      ix += 4;
    } else if (starts("false")) {
      ix += 5;
      r = false;
    } else {
      exc("Expected true or false");
    }
    if (!rend()) {
      excSpare("boolean");
    }
    return r;
  }

  /**
   * Reads a double value.
   * @return The double (number) value of 'js'
   */
  public double rDouble () {
    blanks();
    if (ix == len) {
      exc("Expected a double value");
    }
    int start = ix;
    char ch = tx.charAt(ix);
    if (tx.charAt(ix) == '-') {
      ch = rnext();
    }
    if (ch == '0') {
      ch = rnext();
    } else if (ch >= '1' && ch <='9') {
      ch = rnext();
      while (ch >= '0' && ch <= '9') {
        ch = rnext();
      }
    } else {
      exc("Expected a digit");
    }

    if (ch == '.') {
      ch = rnext();
      if (ch >= '0' && ch <='9') {
        ch = rnext();
        while (ch >= '0' && ch <= '9') {
          ch = rnext();
        }
      } else {
        exc("Expected a digit");
      }
    }

    if (ch == 'e' || ch == 'E') {
      ch = rnext();
      if (ch == '+' || ch == '-') {
        ch = rnext();
      }
      if (ch >= '0' && ch <='9') {
        ch = rnext();
        while (ch >= '0' && ch <= '9') {
          ch = rnext();
        }
      } else {
        exc("Expected a digit");
      }
    }

    int end = ix;
    if (!rend()) {
      excSpare("double");
    }

    return new Double(tx.substring(start, end));
  }

  /**
   * Reads a string value.
   * @return The string value of 'js'
   */
  public String rString () {
    blanks();
    if (ix == len) {
      exc("String");
    }

    char ch = tx.charAt(ix);
    if (ch != '"') {
      exc("Expected a string");
    }
    ch = rnext();

    StringBuilder sb = new StringBuilder();
    for (;;) {
      if (ix == len) {
        exc("Unexpected end of string");
      }

      if (ch == '"') {
        ++ix;
        break;
      }

      if (ch == '\\') {
        ch = rnext();
        if (ch == '"') {
          sb.append('"');
        } else if (ch == '\\') {
          sb.append('\\');
        } else if (ch == '/') {
          sb.append('/');
        } else if (ch == 'b') {
          sb.append('\b');
        } else if (ch == 'f') {
          sb.append('\f');
        } else if (ch == 'n') {
          sb.append('\n');
        } else if (ch == 'r') {
          sb.append('\r');
        } else if (ch == 't') {
          sb.append('\t');
        } else if (ch == 'u') {
          ch = rnext();
          int code = 0;
          if (isHex(ch)) {
            code += 4096 * hexValue(ch);
            ch = rnext();
            if (isHex(ch)) {
              code += 256 * hexValue(ch);
              ch = rnext();
              if (isHex(ch)) {
                code += 16 * hexValue(ch);
                ch = rnext();
                if (isHex(ch)) {
                  code += hexValue(ch);
                  sb.append((char)code);
                } else {
                  exc("Expected a four digits hexadecimal value");
                }
              } else {
                exc("Expected a four digits hexadecimal value");
              }
            } else {
              exc("Expected a four digits hexadecimal value");
            }
          } else {
            exc("Expected a four digits hexadecimal value");
          }
        } else {
          exc("Expected an escape value");
        }

        ch = rnext();
        continue;
      }

      sb.append(ch);
      ch = rnext();
    }

    if (!rend()) {
      excSpare("string");
    }

    return sb.toString();
  }

  /**
   * Reads an array value.
   * @return The array value of 'js'
   */
  public ArrayList<Js> rArray () {
    ArrayList<Js> a = new ArrayList<>();

    blanks();
    if (ix == len) {
      exc("Expected an array");
    }

    char ch = tx.charAt(ix);
    if (ch != '[') {
      exc("Expected an array");
    }
    ++ix;

    blanks();
    if (ix == len) {
      exc("Unexpected end of array");
    }
    ch = tx.charAt(ix);

    if (ch == ']') {
      ++ix;
      if (!rend()) {
        excSpare("array");
      }
      return a;
    }

    for (;;) {
      int start = ix;

      if (ch == '[') {
        skipArray();
      } else if (ch == '{') {
        skipObject();
      } else if (ch == '"') {
        skipString();
      } else {
        skipValue();
      }
      a.add(new Js(tx.substring(start, ix)));

      if (ix == len) {
        exc("Unexpected end of array");
      }

      ch = tx.charAt(ix);
      if (ch == ']') {
        ++ix;
        break;
      }
      if (ch != ',') {
        exc("Expected ','");
      }
      ++ix;

      blanks();
      if (ix == len) {
        exc("Unexpected end of array");
      }
      ch = tx.charAt(ix);
    }

    if (!rend()) {
      excSpare("array");
    }

    return a;
  }

  /**
   * Reads an object value.
   * @return The object value of 'js'
   */
  public HashMap<String, Js> rObject () {
    HashMap<String, Js> h = new HashMap<>();

    blanks();
    if (ix == len) {
      exc("Expected an object");
    }

    char ch = tx.charAt(ix);
    if (ch != '{') {
      exc("Expected an object");
    }
    ++ix;

    blanks();
    if (ix == len) {
      exc("Unexpected end of object");
    }
    ch = tx.charAt(ix);

    if (ch == '}') {
      ++ix;
      if (!rend()) {
        excSpare("object");
      }
      return h;
    }

    int lenCache = len;
    for (;;) {
      int start = ix;
      if (ch != '"') {
        exc("Expected '\"'");
      }
      skipString();
      if (ix == len) {
        exc("Unexpected end of object key");
      }

      len = ix;
      ix = start;
      String key = rString();

      ix = len;
      len = lenCache;

      ch = tx.charAt(ix);
      if (ch != ':') {
        exc("Expected ':'");
      }
      ++ix;

      blanks();
      if (ix == len) {
        exc("Expected an object value");
      }


      start = ix;
      ch = tx.charAt(ix);
      if (ch == '[') {
        skipArray();
      } else if (ch == '{') {
        skipObject();
      } else if (ch == '"') {
        skipString();
      } else {
        skipValue();
      }
      h.put(key, new Js(tx.substring(start, ix)));

      if (ix == len) {
        exc("Unexpected end of object value");
      }

      ch = tx.charAt(ix);
      if (ch == '}') {
        ++ix;
        break;
      }
      if (ch != ',') {
        exc("Expected ','");
      }
      ++ix;

      blanks();
      if (ix == len) {
        exc("Unexpected end of object");
      }
      ch = tx.charAt(ix);
    }

    if (!rend()) {
      excSpare("object");
    }

    return h;
  }

  @Override
  public String toString () {
    return tx;
  }

  static boolean isHex (char ch) {
    return (ch >= '0' && ch <= '9') ||
           (ch >= 'a' && ch <= 'f') ||
           (ch >= 'A' && ch <= 'F');
  }

  static int hexValue (char ch) {
    switch (ch) {
      case '0': return 0;
      case '1': return 1;
      case '2': return 2;
      case '3': return 3;
      case '4': return 4;
      case '5': return 5;
      case '6': return 6;
      case '7': return 7;
      case '8': return 8;
      case '9': return 9;
      case 'a':
      case 'A': return 10;
      case 'b':
      case 'B': return 11;
      case 'c':
      case 'C': return 12;
      case 'd':
      case 'D': return 13;
      case 'e':
      case 'E': return 14;
      case 'f':
      case 'F': return 15;
    }
    throw new IllegalArgumentException("Bad hexadecimal value");
  }

  /**
   * Writes a null value
   * @return Js value
   */
  public static Js write() {
    return new Js("null");
  }

  /**
   * Writes a boolean value
   * @param value value to write
   * @return Js value
   */
  public static Js write(boolean value) {
    return new Js(value ? "true" : "false");
  }

  /**
   * Writes a long value
   * @param value value to write
   * @return Js value
   */
  public static Js write(long value) {
    return new Js(String.valueOf(value));
  }

  /**
   * Writes a double value
   * @param value value to write
   * @return Js value
   */
  public static Js write(double value) {
    return new Js(String.valueOf(value));
  }

  /**
   * Writes a string value
   * @param value value to write
   * @return Js value
   */
  public static Js write(String value) {
    StringBuilder sb = new StringBuilder();
    sb.append('"');
    for (int i = 0; i < value.length(); ++i) {
      char ch = value.charAt(i);
      switch (ch) {
        case '"': sb.append("\\\""); break;
        case '\\': sb.append("\\\\"); break;
        default:
          if (ch < ' ') {
            switch(ch) {
              case '\b': sb.append("\\b");break;
              case '\f': sb.append("\\f");break;
              case '\n': sb.append("\\n");break;
              case '\r': sb.append("\\r");break;
              case '\t': sb.append("\\t");break;
              default: sb.append(' ');
            }
          } else {
            sb.append(ch);
          }
      }
    }
    sb.append('"');
    return new Js(sb.toString());
  }

  /**
   * Writes an array value
   * @param value value to write
   * @return Js value
   */
  public static Js write(List<Js> value) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(String.join(
      ",",
      value.stream().map(js -> js.toString()).collect(Collectors.toList())
    ));
    sb.append(']');
    return new Js(sb.toString());
  }

  /**
   * Writes an object value
   * @param value value to write
   * @return Js value
   */
  public static Js write(Map<String, Js> value) {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append(String.join(
      ",",
      value.entrySet().stream().map(e ->
        write(e.getKey()).toString() + ":" + e.getValue().toString()
      ).collect(Collectors.toList())
    ));
    sb.append('}');
    return new Js(sb.toString());
  }

}
