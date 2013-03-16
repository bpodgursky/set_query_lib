package set_query_lib.collector;

public abstract class ObjectCollector<E> {
	public abstract void collectInternal(E item);
  public void collect(E item){
    collectInternal(item);
  }
}
