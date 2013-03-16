package set_query_lib.collector;

import set_query_lib.Pair;
import set_query_lib.collector.ObjectCollector;

import java.util.Set;
import java.util.concurrent.Semaphore;

public class MeasuredCollector<K> extends ObjectCollector<Pair<Set<K>,Long>> {
  private long expectedToCollect;
  private long collected;
  private final Semaphore lock = new Semaphore(1);
  private final ObjectCollector<Pair<Set<K>,Long>> collector;

  public MeasuredCollector(long expectedToCollect, ObjectCollector<Pair<Set<K>,Long>> collector) throws InterruptedException {
    this.expectedToCollect = expectedToCollect;
    this.collector = collector;
    this.collected = 0;
    this.lock.acquire();
  }

  public Semaphore getLock(){
    return lock;
  }

  public synchronized void collectInternal(Pair<Set<K>, Long> item){
    collector.collect(item);
    collected++;

    if(collected == expectedToCollect){
      lock.release();
    }
  }
}