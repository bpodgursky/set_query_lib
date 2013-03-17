package com.bpodgursky.set_query_lib.node;

public class QueryAddStrat implements AddStrat<QueryNode>{

  @Override
  public void eachParent(QueryNode currentNode, int[] toInsert, int index) {}

  @Override
  public void atLeaf(QueryNode currentNode) {
    currentNode.incrementCount();
  }

  @Override
  public QueryNode[] createArray(int length) {
    return new QueryNode[length];
  }

  @Override
  public QueryNode[] createArray(QueryNode item) {
    return new QueryNode[]{item};
  }

  @Override
  public QueryNode createEmptyNode() {
    return new QueryNode(new int[]{}, new QueryNode[0], 0);
  }

  public QueryNode createNewNode(int[] value){
    return new QueryNode(value, new QueryNode[0], 1);
  }

  @Override
  public QueryNode split(QueryNode currentNode, int[] secondData) {
    QueryNode newChild = new QueryNode(secondData,
        currentNode.getChildren(),
        currentNode.getCount());
    newChild.setCount(1);
    currentNode.setCount(0);
    return newChild;
  }
}
