package set_query_lib;

public class Pair<K, V> {
	 
  private final K first;
  private final V second;
 
  private Pair(K k,V v) {
    first = k;
    second = v;   
  }

  public static <K, V> Pair<K, V> of(K k, V v){
    return new Pair<K, V>(k, v);
  }
 
  public K first() {
    return first;
  }
 
  public V second() {
    return second;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pair)) return false;

    Pair pair = (Pair) o;

    if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
    if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = first != null ? first.hashCode() : 0;
    result = 31 * result + (second != null ? second.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Pair{" +
        "first=" + first +
        ", second=" + second +
        '}';
  }
}
