package set_query_lib;

import java.util.*;

public class SampleFrequencyOrderedMapper<K> extends KeyMapper<K> {
  public static final int DEFAULT_SAMPLE_SIZE = 1000;

  private final Map<K, Integer> keyToIndex = new HashMap<K, Integer>();
  private final Map<Integer, K> indexToK = new HashMap<Integer, K>();

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
    Map<K, Integer> naiveIndex = new HashMap<K, Integer>();
    Map<Integer, K> naiveIndexInv = new HashMap<Integer, K>();
    Map<Integer, Integer> keyToCount = new HashMap<Integer, Integer>();
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

    TreeMap<Integer, Integer> sorted = new TreeMap<Integer, Integer>(new ValueComparator<Integer, Integer>(keyToCount));
    sorted.putAll(keyToCount);

    for(Integer key: sorted.descendingKeySet()){
      addMapping(naiveIndexInv.get(key));
    }
  }
}
