package com.bpodgursky.set_query_lib;

import com.google.common.collect.Maps;

import java.util.*;

public class SampleFrequencyOrderedMapper<K> extends KeyMapper<K> {
  public static final int DEFAULT_SAMPLE_SIZE = 1000;

  private final Map<K, Integer> keyToIndex = Maps.newHashMap();
  private final Map<Integer, K> indexToK = Maps.newHashMap();

  private int maxIndex = 0;
  private int sampleSize;

  public SampleFrequencyOrderedMapper(){
    this(DEFAULT_SAMPLE_SIZE);
  }

  public SampleFrequencyOrderedMapper(int sampleSize){
     this.sampleSize = sampleSize;
  }

  @Override
  public int getIndex(K value) {
    if(keyToIndex.containsKey(value)){
      return keyToIndex.get(value);
    }
    return addMapping(value);
  }

  @Override
  public int getSampleSize() {
    return sampleSize;
  }

  private synchronized int addMapping(K value){
    keyToIndex.put(value, maxIndex);
    indexToK.put(maxIndex, value);
    return maxIndex++;
  }

  @Override
  public K getValue(int index) {
    if(index <= maxIndex){
      return indexToK.get(index);
    }

    throw new IllegalArgumentException("Index "+index+" not mapped to key!");
  }

  @Override
  public void offerSample(List<Set<K>> samples) {

    int keys = 0;
    Map<K, Integer> naiveIndex = Maps.newHashMap();
    Map<Integer, K> naiveIndexInv = Maps.newHashMap();
    Map<Integer, Integer> keyToCount = Maps.newHashMap();
    for(Set<K> sample: samples){
      for(K value: sample){
        if(!naiveIndex.containsKey(value)){
          naiveIndex.put(value, keys);
          naiveIndexInv.put(keys, value);
          keyToCount.put(keys, 1);
          keys++;
        }else{
          keyToCount.put(naiveIndex.get(value), keyToCount.get(naiveIndex.get(value))+1);
        }
      }
    }

    TreeMap<Integer, Integer> sorted = Maps.newTreeMap(new ValueComparator<Integer, Integer>(keyToCount));
    sorted.putAll(keyToCount);

    for(Integer key: sorted.descendingKeySet()){
      addMapping(naiveIndexInv.get(key));
    }
  }
}
