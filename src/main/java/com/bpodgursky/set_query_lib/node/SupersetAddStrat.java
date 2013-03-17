package com.bpodgursky.set_query_lib.node;

public class SupersetAddStrat implements AddStrat<SupersetNode> {
  @Override
  public void eachParent(SupersetNode currentNode, int[] toInsert, int index) {
    currentNode.setAllBelow(currentNode.getAllBelow().add(toInsert, index, toInsert.length));
  }

  @Override
  public void atLeaf(SupersetNode currentNode) {
    currentNode.incrementCount();
  }

  @Override
  public SupersetNode[] createArray(int length) {
    return new SupersetNode[length];
  }

  @Override
  public SupersetNode[] createArray(SupersetNode item) {
    return new SupersetNode[]{item};
  }

  @Override
  public SupersetNode createNewNode(int[] value) {
    return new SupersetNode(value, createArray(0), 1);
  }

  @Override
  public SupersetNode createEmptyNode() {
    return new SupersetNode(new int[]{}, createArray(0), 0);
  }

  @Override
  public SupersetNode split(SupersetNode currentNode, int[] secondData) {
    SupersetNode dn = new SupersetNode(secondData,
        currentNode.getChildren(),
        currentNode.getCount());
    dn.setAllBelow(currentNode.getAllBelow());
    currentNode.setCount(0);
    return dn;
  }
}
