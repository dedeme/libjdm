import es.dm.Std;
import static es.dm.Std.fpath;


public class FileTests {
  static class Wrapper {
    String value;
  }
  public static void run () {
    System.out.println("File Tests");

    String home = Std.home();
    assert Std.fexists(home);
    Std.del(home);
    Std.mkdir(home);

    String path = fpath(home, "f1.txt");
    assert !Std.fexists(path);
    Std.write(path, "");
    assert Std.fexists(path);

    Std.append(path, "123\n45");
    assert Std.readLines(path).size() == 2;

    Wrapper r = new Wrapper();
    r.value = "";
    Std.eachLine(path, (l -> r.value += l + "*\n" ));
    Std.write(path, r.value);
    assert Std.read(path).equals("123*\n45*");

    Std.write(fpath(home, "f2.txt"), "Second");
    assert Std.dir(home).length == 2;

    String zipDir = fpath(home, "zipDir");
    Std.mkdir(zipDir);
    assert Std.copy(fpath(home, "f1.txt"), fpath(zipDir, "f1.txt"));
    assert Std.copy(fpath(home, "f2.txt"), fpath(zipDir, "f2.txt"));

    assert Std.dir(zipDir).length == 2;
    assert Std.read(fpath(zipDir, "f1.txt")).equals("123*\n45*");
    assert Std.read(fpath(zipDir, "f2.txt")).equals("Second");

    String zipFile = fpath(home, "zipDir.zip");
    Std.zip(zipDir, zipFile);
    Std.del(zipDir);
    assert Std.fexists(zipFile);
    assert !Std.fexists(zipDir);

    Std.unzip(zipFile, home);
    assert Std.dir(zipDir).length == 2;
    assert Std.read(fpath(zipDir, "f1.txt")).equals("123*\n45*");
    assert Std.read(fpath(zipDir, "f2.txt")).equals("Second");

    Std.del(home);

    System.out.println("    Finished");
  }
}
