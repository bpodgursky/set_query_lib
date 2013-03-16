package set_query_lib.node;

import set_query_lib.IntBitSet;

public class SupersetNode extends TrieNode<SupersetNode> {

  private IntBitSet allBelow;
  private long count;

  public SupersetNode(int[] id, SupersetNode[] children, long count) {
    super(id, children);

    this.allBelow = new IntBitSet(id);
    this.count = count;
  }

  @Override
  public boolean isReal() {
    return count != 0;
  }

  public void setAllBelow(IntBitSet value){
    this.allBelow = value;
  }

  public IntBitSet getAllBelow() {
    return allBelow;
  }

  public void incrementCount(){
    count++;
  }

  public long getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
