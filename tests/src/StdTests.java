import es.dm.Std;

public class StdTests {
  public static void run () {
    System.out.println("Std Tests");

    assert Std.cmd("ls", "-a").startsWith(".\n..\n");

    double n = Std.nOfStr(Std.nToStr(1.205, 0));
    assert Std.eq0(n, 1);

    n = Std.nOfStr(Std.nToStr(1.205, 2));
    assert Std.eq2(n, 1.21);

    assert Std.nToIso(1234.232, 0).equals("1.234");

    n = Std.nOfIso(Std.nToIso(1234.232, 0));
    assert Std.eq0(n, 1234);

    n = Std.nOfIso(Std.nToIso(1234.232, 2));
    assert Std.eq2(n, 1234.23);

    assert Std.nToUs(1234.232, 0).equals("1,234");

    n = Std.nOfUs(Std.nToUs(1234.232, 0));
    assert Std.eq0(n, 1234);

    n = Std.nOfUs(Std.nToUs(1234.232, 2));
    assert Std.eq2(n, 1234.23);

    System.out.println("    Finished");
  }
}
