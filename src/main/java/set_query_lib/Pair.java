package set_query_lib;

public class Pair<K, V> {
	 
  protected final K first;
  protected final V second;
 
  public Pair(K k,V v) {  
  	if(k == null || v == null) throw new IllegalArgumentException("null field");
  	
    first = k;
    second = v;   
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
