// Copyright 17-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/** Random box */
public class Rbox<T> implements Iterable<T>, Iterator<T> {
  List<T> els;
  int pointer = 0;

  /**
   * @param els Elements of Rbox. It must have at least one element.
   */
  public Rbox(List<T> els) {
    this.els = new ArrayList<T>();
    this.els.addAll(els);
  }

  @Override
  public boolean hasNext () {
    return true;
  }

  @Override
  public T next () {
    if (pointer == 0) {
      Std.shuffle(els);
      pointer = els.size();
    }
    return els.get(--pointer);
  }

  @Override
  public Iterator<T> iterator () {
    return this;
  }

  /** Class may be made with 'Rbox.pair' to use with 'Rbox.mk' */
  public static class Pair<T> {
    int ammount;
    T element;
    private Pair () {
    }
  }

  /**
   * Function to use with 'Rbox.mk'
   * @param <T> Generic
   * @param ammount Number of elements
   * @param element Element to repeat
   * @return A Rbox.Pair
   */
  public static <T> Pair<T> pair(int ammount, T element) {
    Pair<T> r = new Pair<>();
    r.ammount = ammount;
    r.element = element;
    return r;
  }

  /**
   * Creates a Rbox with repeated elements. For example:<pre>
   *   box = Rbox.mk(new ArrayList&lt;Rbox.Pair&lt;Integer&gt;&gt;() {{
   *     add(Rbox.pair(2, 100));
   *     add(Rbox.pair(3, 101));
   *   }});
   * </pre>
   * @param <T> Generic
   * @param els Elements of box.
   * @return A new Rbox
   */
  public static <T> Rbox<T> mk (List<Pair<T>> els) {
    return new Rbox<T>(els.stream().reduce(
      new ArrayList<T>(),
      (seed, p) -> {
        for (int i = 0; i < p.ammount; ++i) {
          seed.add(p.element);
        }
        return seed;
      },
      (e, f) -> e
    ));
  }
}
