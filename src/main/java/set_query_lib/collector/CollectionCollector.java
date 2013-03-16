package set_query_lib.collector;

import java.util.Collection;

public class CollectionCollector<E> extends ObjectCollector<E> {

  private final Collection<E> objects;

  public CollectionCollector(Collection<E> objects) {
    this.objects = objects;
  }

  @Override
  public synchronized void collectInternal(E item) {
    objects.add(item);
  }
}
