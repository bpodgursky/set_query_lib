package set_query_lib.node;

import set_query_lib.Pair;

import java.util.Set;

public class EmitQueryNode<K> implements EmitStrat<QueryNode, K, Pair<Set<K>, Long>>{
  @Override
  public Pair<Set<K>, Long> emit(QueryNode node, Set<K> id) {
    return Pair.of(id, node.getCount());
  }
}