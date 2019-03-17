// Copyright 04-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>


import es.dm.Std;

public class Main {
    public static void main(String[] args) {
      Std.sysInit("libjdmTests");
      StdTests.run();
      DateTests.run();
      FileTests.run();
      JsTests.run();
      RboxTests.run();
      CrypTests.run();
    }
}
