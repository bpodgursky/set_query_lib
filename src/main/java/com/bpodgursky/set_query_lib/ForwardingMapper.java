package com.bpodgursky.set_query_lib;

public class ForwardingMapper<K> extends KeyMapper<K> {

  private final KeyMapper<K> internal;

  public ForwardingMapper(KeyMapper<K> internalMapper){
    this.internal = internalMapper;
  }

  @Override
  public int getSampleSize(){
    return 0;
  }

  @Override
  public int getIndex(K value) {
    return internal.getIndex(value);
  }

  @Override
  public K getValue(int index) {
    return internal.getValue(index);
  }
}
