package com.bpodgursky.set_query_lib.node;


import org.apache.log4j.Logger;

public class QueryNode extends TrieNode<QueryNode> {
  private static final Logger LOG = Logger.getLogger(QueryNode.class);

  private long count;

  public QueryNode(int[] id, QueryNode[] children, long count){
    super(id, children);
    this.count = count;
  }

  @Override
  public boolean isReal() {
    return count != 0;
  }

  public long getCount(){
    return count;
  }

  public void setCount(long count){
    this.count = count;
  }

  @Override
  public String toString() {
    return "QueryNode{" +
        "count=" + count +
        '}';
  }

  //  package

  protected void incrementCount(){
    count++;
  }
}