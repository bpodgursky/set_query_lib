package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.set_query_lib.Pair;
import com.bpodgursky.set_query_lib.collector.ObjectCollector;
import com.bpodgursky.set_query_lib.node.DataNode;
import com.bpodgursky.set_query_lib.node.QueryNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

public class SimpleSubsetExecutor<T, K> extends SubsetQueryExecutor<T, K> {
  @Override
  public void call(QueryNode sn, int[] idCopy, LinkedList<DataNode> frontierHere, ObjectCollector<Pair<Set<K>, Long>> collector) throws IOException {
    querier.supersetQuery(sn, idCopy, 0, frontierHere, collector);
  }
}
