package com.bpodgursky.set_query_lib.node;

public interface AddStrat<A extends TrieNode<A>> {

  public void eachParent(A currentNode, int[] toInsert, int index);
  public void atLeaf(A currentNode);

  public A[] createArray(int length);
  public A[] createArray(A item);

  public A createNewNode(int[] value);
  public A createEmptyNode();

  public A split(A currentNode, int[] secondData);
}