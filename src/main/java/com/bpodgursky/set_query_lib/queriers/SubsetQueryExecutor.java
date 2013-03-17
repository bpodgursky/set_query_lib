package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.set_query_lib.Pair;
import com.bpodgursky.set_query_lib.collector.ObjectCollector;
import com.bpodgursky.set_query_lib.node.DataNode;
import com.bpodgursky.set_query_lib.node.QueryNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

public abstract class SubsetQueryExecutor<T, K> {

  protected BatchSubsetCountQuerier<T, K> querier;

  public void setQuerier(BatchSubsetCountQuerier<T, K> querier){
    this.querier = querier;
  }

  public abstract void call(QueryNode sn,
                       int[] idCopy,
                       LinkedList<DataNode> frontierHere,
                       ObjectCollector<Pair<Set<K>, Long>> collector) throws IOException;
}