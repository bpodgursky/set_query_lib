package com.bpodgursky.set_query_lib.node;

import com.bpodgursky.set_query_lib.IntBitSet;

public class DataNode extends TrieNode<DataNode> {

  private IntBitSet allBelow;
  private long cumulativeBelow;

  public DataNode(int[] id, DataNode[] children, long size) {
    super(id, children);

    this.cumulativeBelow = size;
    this.allBelow = new IntBitSet(id);
  }

  public IntBitSet getAllBelow() {
    return allBelow;
  }

  public long getCumulativeBelow(){
    return cumulativeBelow;
  }

  public void incrementCumulativeBelow(){
    cumulativeBelow++;
  }

  @Override
  public boolean isReal() {
    long val = this.cumulativeBelow;
    for(DataNode sn: this.getChildren()) val-=sn.cumulativeBelow;
    return val != 0;
  }

  public void setAllBelow(IntBitSet value){
    this.allBelow = value;
  }
}
