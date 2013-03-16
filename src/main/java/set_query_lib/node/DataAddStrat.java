package set_query_lib.node;

public class DataAddStrat implements AddStrat<DataNode> {

  public void eachParent(DataNode currentNode, int[] toInsert, int index) {
    currentNode.setAllBelow(currentNode.getAllBelow().add(toInsert, index, toInsert.length));
    currentNode.incrementCumulativeBelow();
  }

  @Override
  public DataNode createNewNode(int[] value) {
    return new DataNode(value, createArray(0), 1);
  }

  @Override
  public DataNode createEmptyNode() {
    return new DataNode(new int[]{}, createArray(0), 0);
  }

  @Override
  public DataNode split(DataNode currentNode, int[] secondData) {
    DataNode dn = new DataNode(secondData,
        currentNode.getChildren(),
        currentNode.getCumulativeBelow()-1);
    dn.setAllBelow(currentNode.getAllBelow());
    return dn;
  }

  @Override
  public void atLeaf(DataNode currentNode) {}

  @Override
  public DataNode[] createArray(int length) {
    return new DataNode[length];
  }

  @Override
  public DataNode[] createArray(DataNode item) {
    return new DataNode[]{item};
  }
}
