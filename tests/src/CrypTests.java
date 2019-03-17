import es.dm.Cryp;

public class CrypTests {
  static boolean test (String tx, String k) {
    return Cryp.decode(Cryp.encode(tx, k), k).equals(tx);
  }

  public static void run () {
    System.out.println("Cryp Tests");

    String b64 = Cryp.genk(6);
    assert(6 == b64.length());

    String s = "deme";
    b64 = Cryp.key(s, 6);
    assert b64.equals("wiWTB9");

    s = "Generaro";
    b64 = Cryp.key(s, 5);
    assert b64.equals("Ixy8I");

    s = "Generara";
    b64 = Cryp.key(s, 5);
    assert b64.equals("0DIih");

    assert test("01", "abc");
    assert test("11", "abcd");
    assert test("", "abc");
    assert test("a", "c");
    assert test("ab c", "xxx");
    assert test("\n\ta€b c", "abc");

    System.out.println("    Finished");
  }
}
