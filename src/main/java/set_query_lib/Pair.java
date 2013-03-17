package set_query_lib;

public class Pair<K, V> {
	 
  private final K first;
  private final V second;
 
  private Pair(K k,V v) {
  	if(k == null || v == null) throw new IllegalArgumentException("null field");
  	
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
 
  public String toString() { 
    return "(" + first + ", " + second + ")";  
  }
  
  @Override
  public boolean equals(Object p){
  	if(p == null || !(p instanceof Pair)) return false;  	
  	Pair otherPair = (Pair)p;
  	
  	return first.equals(otherPair.first) && second.equals(otherPair.second);
  }
}
