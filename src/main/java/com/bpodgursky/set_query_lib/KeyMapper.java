package com.bpodgursky.set_query_lib;

import java.util.*;

public abstract class KeyMapper<K> {

  public void offerSample(List<Set<K>> samples){
    //  nothing by default
  }

  public int getSampleSize(){
    return 1;
  }

  public abstract int getIndex(K value);

  public abstract K getValue(int index);

  public Set<K> getValues(int[] keys){
    Set<K> values = new HashSet<K>();
    for (int key : keys) {
      values.add(getValue(key));
    }
    return values;
  }

  public int[] getKeys(Set<K> values){
    int[] keys = new int[values.size()];
    int count = 0;
    for(K value: values){
      keys[count++] = getIndex(value);
    }
    Arrays.sort(keys);
    return keys;
  }
}
