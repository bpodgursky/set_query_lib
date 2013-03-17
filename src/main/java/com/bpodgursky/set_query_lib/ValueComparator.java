package com.bpodgursky.set_query_lib;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator<A extends Comparable<? super A>, B extends Comparable<? super B>> implements Comparator<A> {
  private final Map<A, B> base; 
  
  public ValueComparator(Map<A, B> reference){
    base = reference;
  }
  
  public int compare(A a, A b){
    int diffValue = base.get(a).compareTo(base.get(b));
    if(diffValue != 0){
      return diffValue;
    }

    return a.compareTo(b);
  }
}