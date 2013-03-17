package com.bpodgursky.set_query_lib.node;

public abstract class TrieNode<A extends TrieNode<A>> {

  private int[] datas;
  private A[] children;

  public TrieNode(int[] id, A[] children) {
    this.datas = id;
    this.children = children;
  }

  public A[] getChildren(){
    return children;
  }

  public int[] getData(){
    return datas;
  }

  public abstract boolean isReal();

  //  package

  protected void setData(int[] data){
    this.datas = data;
  }

  protected void setChildren(A[] children){
    this.children = children;
  }
}
