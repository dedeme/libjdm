// Copyright 17-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

public class Cryp {
  /**
   * Returns a random (possibly not valid B64) String of length 'length' with
   * B64 characters.
   * @param length Length
   * @return A String
   */
  public static String genk(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("'" + length + "' is less than 0");
    }

    String alpha = B64.alpha;
    int len = alpha.length();
    char[] r = new char[length];
    for (int i = 0; i < length; ++i) {
      r[i] = alpha.charAt(Std.rndi(len));
    }
    return new String(r);
  }

  /**
   * Codifies 'k' irreversibly in a B64 String of length 'length'.
   * @param s Seed
   * @param length Length of return
   * @return a String
   */
  public static String key(String s, int length) {
    if (s.length() == 0) {
      throw new IllegalArgumentException("Key is an empty String");
    }

    String k0 = s + "codified in irreversibleDeme is good, very good!\n\r8@@";
    String b64 = B64.encode(k0);
    byte[] k = B64.decodeBytes(b64);
    int lenk = k.length;

    int sum = 0;
    for (int i = 0; i < lenk; ++i) {
      byte b = k[i];
      sum += (int)(b < 0 ? 256 + b : b);
    }

    int length2 = length + lenk;
    byte[] bs = new byte[length2];
    byte[] bs1 = new byte[length2];
    byte[] bs2 = new byte[length2];

    int ik = 0;
    int v1, v2, v3, v4;
    for (int i = 0; i < length2; ++i) {
      byte b = k[ik];
      v1 = (int)(b < 0 ? 256 + b : b);

      b = k[v1 % lenk];
      v2 = v1 + (int)(b < 0 ? 256 + b : b);

      b = k[v2 % lenk];
      v3 = v2 + (int)(b < 0 ? 256 + b : b);

      b = k[v3 % lenk];
      v4 = v3 + (int)(b < 0 ? 256 + b : b);

      sum += i + v4;
      bs1[i] = (byte)sum;
      bs2[i] = (byte)sum;
      ++ik;
      if (ik == lenk) {
        ik = 0;
      }
    }

    for (int i = 0; i < length2; ++i) {
      byte b = bs2[i];
      v1 = (int)(b < 0 ? 256 + b : b);

      b = bs2[v1 % length2];
      v2 = v1 + (int)(b < 0 ? 256 + b : b);

      b = bs2[v2 % length2];
      v3 = v2 + (int)(b < 0 ? 256 + b : b);

      b = bs2[v3 % length2];
      v4 = v3 + (int)(b < 0 ? 256 + b : b);

      sum += v4;
      bs2[i] = (byte)sum;
      b = bs1[i];
      bs[i] = (byte)(sum + (int)(b < 0 ? 256 + b : b));
    }

    return B64.encode(bs).substring(0, length);
  }

  /**
   * Encode 'tx' with 'k'.
   * @param tx String to encode
   * @param k Key
   * @return 'tx' encoded
   */
  public static String encode (String tx, String k) {
    if (k.length() == 0) {
      throw new IllegalArgumentException("k is an empty String");
    }

    String b64 = B64.encode(tx);
    int len = b64.length();
    String k2 = key(k, len);

    char[] r = new char[len];
    for (int i = 0; i < len; ++i) {
      r[i] = (char)(b64.charAt(i) + k2.charAt(i));
    }

    return B64.encode(new String(r));
  }

  /**
   * Decode 'tx' with 'k'.
   * @param tx String to edcode
   * @param k Key
   * @return 'tx' decoded
   */
  public static String decode (String tx, String k) {
    if (k.length() == 0) {
      throw new IllegalArgumentException("k is an empty String");
    }

    String b64 = B64.decode(tx);
    int len = b64.length();
    String k2 = key(k, len);

    char[] r = new char[len];
    for (int i = 0; i < len; ++i) {
      r[i] = (char)(b64.charAt(i) - k2.charAt(i));
    }

    return B64.decode(new String(r));
  }

}
