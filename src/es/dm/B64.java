// Copyright 17-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.Base64;

public class B64 {
  B64 () {
  }

  /** B64 alphabet */
  public static String alpha =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  /**
   * Decode a string encoded with 'encodeB64'
   *
   * @param code Codified string
   * @return bytes
   */
  public static byte[] decodeBytes(String code) {
    return Base64.getDecoder().decode(code);
  }

  /**
   * Decode a utf-8 string encoded with 'encodeB64'
   *
   * @param code Codified string
   * @return A String
   */
  public static String decode(String code) {
    try {
      return new String(Base64.getDecoder().decode(code), "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Encode 'in' in Base-64.
   *
   * @param in Data to codify
   * @return Codified String
   */
  public static String encode(byte[] in) {
    return Base64.getEncoder().encodeToString(in);
  }

  /**
   * Encode 'in' in Base-64.
   *
   * @param in Data to codify
   * @return Codified String
   */
  public static String encode(String in) {
    try {
      return Base64.getEncoder().encodeToString(in.getBytes("UTF-8"));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
