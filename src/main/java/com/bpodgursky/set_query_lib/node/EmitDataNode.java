package com.bpodgursky.set_query_lib.node;

import com.bpodgursky.set_query_lib.Pair;

import java.util.Set;

public class EmitDataNode<K> implements EmitStrat<DataNode, K, Pair<Set<K>, Long>>{
  @Override
  public Pair<Set<K>, Long> emit(DataNode node, Set<K> id) {
    return Pair.of(id, node.getCumulativeBelow());
  }
}