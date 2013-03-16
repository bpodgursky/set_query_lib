package set_query_lib.node;

import set_query_lib.KeyMapper;
import set_query_lib.collector.ObjectCollector;
import org.apache.commons.lang.ArrayUtils;

public class RootNode<A extends TrieNode<A>> {

  private AddStrat<A> addStrat;
  private A internalRoot;
  private long distinctEntries;

  public RootNode(AddStrat<A> trieType) {
    this.addStrat = trieType;
    this.internalRoot = trieType.createEmptyNode();
    this.distinctEntries = 0;
  }

  public A getRoot() {
    return internalRoot;
  }

  public long getDistinctEntries() {
    return distinctEntries;
  }

  public synchronized boolean add(int[] node) {
    if (addInternal(node)) {
      distinctEntries++;
      return true;
    }
    return false;
  }

  private boolean addInternal(int[] insertedElement) {

    A currentNode = internalRoot;
    int insertIndex = 0;

    while (true) {

      int[] nodeData = currentNode.getData();
      A[] children = currentNode.getChildren();
      boolean isReal = currentNode.isReal();

      addStrat.eachParent(currentNode, insertedElement, insertIndex);

      int numOverlapping = getIndex(insertIndex, nodeData, insertedElement);
      insertIndex += numOverlapping;

      //  case 1: overlap, same length
      if (numOverlapping == nodeData.length && insertIndex == insertedElement.length) {
        addStrat.atLeaf(currentNode);
        return !isReal;
      }

      //  case 2: overlap, new entry longer than this node's data
      else if (numOverlapping == nodeData.length) {

        int valueToSearch = insertedElement[insertIndex];
        A childToSearch = getChildToSearch(children, valueToSearch);

        // if the present segment isn't one of the current node's children, add it in
        if (childToSearch == null) {

          A[] newChildren = addStrat.createArray(children.length + 1);
          A newNode = addStrat.createNewNode(ArrayUtils.subarray(insertedElement, insertIndex, insertedElement.length));

          int i = 0;
          int j = 0;

          // insert new segment into current node's children while maintaining sorted order of children
          while (i < children.length) {
            if (j == i && children[i].getData()[0] > valueToSearch) {
              newChildren[j] = newNode;
              j++;
            }
            newChildren[j] = children[i];
            i++;
            j++;
          }
          if (j == i) {
            newChildren[j] = newNode;
          }
          // set current node's children and move down the tree
          currentNode.setChildren(newChildren);
          return true;
        } else {
          currentNode = childToSearch;
        }
      } else {

        currentNode.setData(ArrayUtils.subarray(nodeData, 0, numOverlapping));
        A newChild = addStrat.split(currentNode, ArrayUtils.subarray(nodeData, numOverlapping, nodeData.length));

        //  case 4: new entry's data doesn't fit in this node's data
        if (insertIndex < insertedElement.length) {
          A[] newChildren = addStrat.createArray(2);
          newChildren[0] = addStrat.createNewNode(ArrayUtils.subarray(insertedElement, insertIndex, insertedElement.length));
          newChildren[1] = newChild;
          currentNode.setChildren(newChildren);
        } else {
          currentNode.setChildren(addStrat.createArray(newChild));
          addStrat.atLeaf(currentNode);
        }

        return true;
      }
    }
  }

  public <K, O> void writeNodes(KeyMapper<K> mapper,
                             EmitStrat<A, K, O> strat,
                             ObjectCollector<O> collector) {
    writeNodes(internalRoot, new int[]{}, strat, mapper, collector);
  }

  private <K, O> void writeNodes(A node,
                             int[] incrementalId,
                             EmitStrat<A, K, O> strat,
                             KeyMapper<K> mapper,
                             ObjectCollector<O> collector) {
    int[] localId = ArrayUtils.addAll(incrementalId, node.getData());
    collector.collect(strat.emit(node, mapper.getValues(localId)));
    for (A sn : node.getChildren()) {
      writeNodes(sn, localId, strat, mapper, collector);
    }
  }

  private static <N extends TrieNode<N>> N getChildToSearch(N[] children, int data) {
    N childToSearch = null;
    for (N sn : children) {
      int[] childData = sn.getData();
      if (childData[0] == data) {
        childToSearch = sn;
        break;
      } else if (childData[0] > data) {
        break;
      }
    }

    return childToSearch;
  }

  private static int getIndex(int index, int[] nodeData, int[] node) {
    int localIndex = 0;
    int mapIndex = index;
    while (
        localIndex < nodeData.length &&
            mapIndex < node.length &&
            nodeData[localIndex] == node[mapIndex]) {
      mapIndex++;
      localIndex++;
    }

    return localIndex;
  }
}
