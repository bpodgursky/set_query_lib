package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.set_query_lib.Pair;
import com.bpodgursky.set_query_lib.UncaughtRunnable;
import com.bpodgursky.set_query_lib.collector.ObjectCollector;
import com.bpodgursky.set_query_lib.node.DataNode;
import com.bpodgursky.set_query_lib.node.QueryNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolQueryExecutor<T, K> extends SubsetQueryExecutor<T, K> {

  //Pool used to execute concurrent queries wherever possible
  private final ThreadPoolExecutor pool;

  public ThreadPoolQueryExecutor(int threads){
    this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
  }

  @Override
  public void call(final QueryNode sn,
                       final int[] idCopy,
                       final LinkedList<DataNode> frontierHere,
                       final ObjectCollector<Pair<Set<K>, Long>> collector) throws IOException {
    if(shouldSpawn()){
      pool.execute(new UncaughtRunnable() {
        public void runInternal() throws IOException {
          querier.supersetQuery(sn, idCopy, 0, frontierHere, collector);
        }
      });
    }else{
      querier.supersetQuery(sn, idCopy, 0, frontierHere, collector);
    }
  }

  public synchronized boolean shouldSpawn() {
    return pool.getActiveCount() < pool.getMaximumPoolSize();
  }
}
