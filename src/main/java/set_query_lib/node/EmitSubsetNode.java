package set_query_lib.node;

import set_query_lib.Pair;

import java.util.Set;

public class EmitSubsetNode<K> implements EmitStrat<SupersetNode, K, Pair<Set<K>, Long>>{
  @Override
  public Pair<Set<K>, Long> emit(SupersetNode node, Set<K> id) {
    return new Pair<Set<K>, Long>(id, node.getCount());
  }
}
