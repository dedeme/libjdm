
import es.dm.Js;
import es.dm.Std;
import java.util.ArrayList;
import java.util.HashMap;

public class JsTests {
  public static void run () {
    System.out.println("Js Tests");

    assert new Js("  null  ").isNull();

    assert new Js(" true").rBoolean();
    assert !new Js("false").rBoolean();

    assert new Js("0").rDouble() == 0;
    assert new Js("-0").rDouble() == 0;
    assert new Js("12345").rDouble() == 12345;
    assert new Js("-12345").rDouble() == -12345;
    assert new Js("0.12").rDouble() == 0.12;
    assert new Js("-0.12").rDouble() == -0.12;
    assert new Js("12345.12  ").rDouble() == 12345.12;
    assert new Js("  -12345.12").rDouble() == -12345.12;
    assert new Js("0.12e2").rDouble() == 0.12e2;
    assert new Js("-0.12E2").rDouble() == -0.12e2;
    assert new Js("12345.12e2").rDouble() == 12345.12e2;
    assert new Js("-12345.12e2").rDouble() == -12345.12e2;
    assert new Js("0.12e+2").rDouble() == 0.12e2;
    assert new Js("-0.12E-2").rDouble() == -0.12e-2;
    assert new Js("12345.12e-2").rDouble() == 12345.12e-2;
    assert new Js("-12345.12e+2").rDouble() == -12345.12e2;
    assert new Js("0e+2").rDouble() == 0e2;
    assert new Js("-0E-2").rDouble() == -0e-2;
    assert new Js("12345e-2").rDouble() == 12345e-2;
    assert new Js("-12345e+2").rDouble() == -12345e2;

    assert new Js(" \"\"").rString().equals("");
    assert new Js("\"abc\"  ").rString().equals("abc");
    assert new Js("\"\\\\a\\\"b\\\"c\"").rString().equals("\\a\"b\"c");
    assert new Js("\"c\\u0040ñón\"").rString().equals("c@ñón");

    String js = " [ ]  ";
    ArrayList<Js> ajs = new Js(js).rArray();
    assert ajs.size() == 0;

    js = " [ 1, 2]  ";
    ajs = new Js(js).rArray();
    assert ((int)ajs.get(0).rDouble()) == 1;
    assert ((int)ajs.get(1).rDouble()) == 2;

    js = " [ 1, 2, [], [1], [3, 4], {}," +
         " {\"a\" : 3}, {\"a\" : 3, \"b\" : 2}]  ";
    ajs = new Js(js).rArray();
    assert ajs.get(0).toString().equals("1");
    assert ajs.get(1).toString().equals("2");
    assert ajs.get(2).toString().equals("[]");
    assert ajs.get(3).toString().equals("[1]");
    assert ajs.get(4).toString().equals("[3, 4]");
    assert ajs.get(5).toString().equals("{}");
    assert ajs.get(6).toString().equals("{\"a\" : 3}");
    assert ajs.get(7).toString().equals("{\"a\" : 3, \"b\" : 2}");

    js = "{}";
    HashMap<String, Js> hjs = new Js(js).rObject();
    assert hjs.size() == 0;

    js = "{\"a\" : false } ";
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("false ");

    js = "{\"a\" : [3, false], \"b\": true, \"c\": [null, 44] } ";
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("[3, false]");
    assert hjs.get("b").toString().equals("true");
    assert hjs.get("c").toString().equals("[null, 44] ");

    js = "{\"a\" : [3, {}], \"b\": {\"qb\": \"qbx\"}, \"c\": [null, {}] } ";
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("[3, {}]");
    assert hjs.get("b").toString().equals("{\"qb\": \"qbx\"}");
    assert hjs.get("c").toString().equals("[null, {}] ");

    assert Js.write().isNull();

    assert Js.write(true).rBoolean();
    assert !Js.write(false).rBoolean();

    assert Js.write(0).rDouble() == 0;
    assert Js.write(-0).rDouble() == 0;
    assert Js.write(12345).rDouble() == 12345;
    assert Js.write(-12345).rDouble() == -12345;
    assert Js.write(0.12).rDouble() == 0.12;
    assert Js.write(-0.12).rDouble() == -0.12;
    assert Js.write(12345.12  ).rDouble() == 12345.12;
    assert Js.write(-12345.12).rDouble() == -12345.12;
    assert Js.write(0.12e2).rDouble() == 0.12e2;
    assert Js.write(-0.12E2).rDouble() == -0.12e2;
    assert Js.write(12345.12e2).rDouble() == 12345.12e2;
    assert Js.write(-12345.12e2).rDouble() == -12345.12e2;
    assert Js.write(0.12e+2).rDouble() == 0.12e2;
    assert Js.write(-0.12E-2).rDouble() == -0.12e-2;
    assert Js.write(12345.12e-2).rDouble() == 12345.12e-2;
    assert Js.write(-12345.12e+2).rDouble() == -12345.12e2;
    assert Js.write(0e+2).rDouble() == 0e2;
    assert Js.write(-0E-2).rDouble() == -0e-2;
    assert Js.write(12345e-2).rDouble() == 12345e-2;
    assert Js.write(-12345e+2).rDouble() == -12345e2;

    assert Js.write("").rString().equals("");
    assert Js.write("abc").rString().equals("abc");
    assert Js.write("\\a\"b\"c\n\t").rString().equals("\\a\"b\"c\n\t");
    assert Js.write("c@ñón").rString().equals("c@ñón");

    js = Js.write(new ArrayList<>()).toString();
    ajs = new Js(js).rArray();
    assert ajs.size() == 0;

    js = Js.write(new ArrayList<Js>() {{
      add(Js.write(1));
      add(Js.write(2));
    }}).toString();
    ajs = new Js(js).rArray();
    assert ((int)ajs.get(0).rDouble()) == 1;
    assert ((int)ajs.get(1).rDouble()) == 2;

    js = Js.write(new ArrayList<Js>() {{
      add(Js.write(1));
      add(Js.write(2));
      add(Js.write(new ArrayList<>()));
      add(Js.write(new ArrayList<Js>() {{ add(Js.write(1)); }}));
      add(Js.write(new ArrayList<Js>() {{
        add(Js.write(3));
        add(Js.write(4));
      }}));
      add(Js.write(new HashMap<>()));
      add(Js.write(new HashMap<String, Js>() {{
        put("a", Js.write(3));
      }}));
      add(Js.write(new HashMap<String, Js>() {{
        put("a", Js.write(3));
        put("b", Js.write(2));
      }}));
    }}).toString();

    ajs = new Js(js).rArray();
    assert ajs.get(0).toString().equals("1");
    assert ajs.get(1).toString().equals("2");
    assert ajs.get(2).toString().equals("[]");
    assert ajs.get(3).toString().equals("[1]");
    assert ajs.get(4).toString().equals("[3,4]");
    assert ajs.get(5).toString().equals("{}");
    assert ajs.get(6).toString().equals("{\"a\":3}");
    assert ajs.get(7).toString().equals("{\"a\":3,\"b\":2}");

    js = Js.write(new HashMap<>()).toString();
    hjs = new Js(js).rObject();
    assert hjs.size() == 0;

    js = Js.write(new HashMap<String, Js>() {{
      put("a", Js.write(false));
    }}).toString();
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("false");

    js = Js.write(new HashMap<String, Js>() {{
      put("a", Js.write(new ArrayList<Js>() {{
        add(Js.write(3));
        add(Js.write(false));
      }}));
      put("b", Js.write(true));
      put("c", Js.write(new ArrayList<Js>() {{
        add(Js.write());
        add(Js.write(44));
      }}));
    }}).toString();
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("[3,false]");
    assert hjs.get("b").toString().equals("true");
    assert hjs.get("c").toString().equals("[null,44]");

    js = Js.write(new HashMap<String, Js>() {{
      put("a", Js.write(new ArrayList<Js>() {{
        add(Js.write(3));
        add(Js.write(new HashMap<>()));
      }}));
      put("b", Js.write(new HashMap<String, Js>() {{
        put("qb", Js.write("qbx"));
      }}));
      put("c", Js.write(new ArrayList<Js>() {{
        add(Js.write());
        add(Js.write(new HashMap<>()));
      }}));
    }}).toString();
    hjs = new Js(js).rObject();
    assert hjs.get("a").toString().equals("[3,{}]");
    assert hjs.get("b").toString().equals("{\"qb\":\"qbx\"}");
    assert hjs.get("c").toString().equals("[null,{}]");

    double n = Js.write(Std.fix(1234.232, 2)).rDouble();
    assert Std.eq2(n, 1234.23);

    System.out.println("    Finished");
  }
}
