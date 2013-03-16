package set_query_lib.node;

import set_query_lib.Pair;

import java.util.Set;

public class EmitDataNode<K> implements EmitStrat<DataNode, K, Pair<Set<K>, Long>>{
  @Override
  public Pair<Set<K>, Long> emit(DataNode node, Set<K> id) {
    return new Pair<Set<K>, Long>(id, node.getCumulativeBelow());
  }
}