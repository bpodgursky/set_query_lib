package com.bpodgursky.set_query_lib.node;

import com.bpodgursky.set_query_lib.Pair;

import java.util.Set;

public class EmitSubsetNode<K> implements EmitStrat<SupersetNode, K, Pair<Set<K>, Long>>{
  @Override
  public Pair<Set<K>, Long> emit(SupersetNode node, Set<K> id) {
    return Pair.of(id, node.getCount());
  }
}
