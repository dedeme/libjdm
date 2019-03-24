import es.dm.Std;
import java.util.stream.*;
import es.dm.Rbox;
import java.util.ArrayList;

public class RboxTests {
  public static void run () {
    System.out.println("Rbox Tests");

    Rbox<Integer> box = new Rbox<>(
      IntStream.range(0, 4).boxed().collect(Collectors.toList())
    );
//    Std.toStream(box).limit(8).forEach(System.out::println);

    box = Rbox.mk(new ArrayList<Rbox.Pair<Integer>>() {{
      add(Rbox.pair(2, 100));
      add(Rbox.pair(3, 101));
    }});
//    Std.toStream(box).limit(10).forEach(System.out::println);

    System.out.println("    Finished");
  }
}
