package set_query_lib.runners;

import set_query_lib.*;
import set_query_lib.collector.MeasuredCollector;
import set_query_lib.collector.ObjectCollector;
import set_query_lib.node.*;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.util.*;

public class BatchSubsetCountQuerier<T, K> {

  private final RootNode<DataNode> dataRoot;
  private final RootNode<QueryNode> queryRoot;

  private final KeyMapper<K> mapper;
  private final ObjectCollector<Pair<Set<K>, Long>> output;
  private final SubsetQueryExecutor<T, K> executor;

  private BatchSubsetCountQuerier(Iterator<T> values,
                                  RecordExtractor<T, K> extractor,
                                  KeyMapper<K> mapper,
                                  SubsetQueryExecutor<T, K> executor,
                                  ObjectCollector<Pair<Set<K>, Long>> output) {

    this.dataRoot = new RootNode<DataNode>(new DataAddStrat());
    this.queryRoot = new RootNode<QueryNode>(new QueryAddStrat());

    this.mapper = mapper;
    this.output = output;
    this.executor = executor;

    List<Set<K>> samples = new ArrayList<Set<K>>();
    while (samples.size() < mapper.getSampleSize() && values.hasNext()) {
      samples.add(extractor.getKeys(values.next()));
    }

    mapper.offerSample(samples);

    for (Set<K> sample : samples) {
      dataRoot.add(mapper.getKeys(sample));
    }

    while (values.hasNext()) {
      dataRoot.add(mapper.getKeys(extractor.getKeys(values.next())));
    }
  }

  public synchronized void query(Set<K> query) {
    queryRoot.add(mapper.getKeys(query));
  }

  public synchronized void query(Iterator<Set<K>> queries) {
    while (queries.hasNext()) {
      query(queries.next());
    }
  }

  public synchronized void flushQueries() throws IOException, InterruptedException {
    MeasuredCollector<K> collector = new MeasuredCollector<K>(queryRoot.getDistinctEntries(), output);
    this.executor.setQuerier(this);
    for (final QueryNode child : queryRoot.getRoot().getChildren()) {
      this.executor.call(child, new int[]{},
          new LinkedList<DataNode>(Arrays.<DataNode>asList(dataRoot.getRoot().getChildren())), collector);
    }
    collector.getLock().acquire();
  }


  /**
   * Given a frontier on another trie, find the count of nodes on the
   * other trie that are a superset of this node (and call recursively
   * on children)
   *
   * @throws IOException
   */
  protected void supersetQuery(QueryNode currentNode,
                               int[] incrementalId,
                               int position,
                               LinkedList<DataNode> frontier,
                               ObjectCollector<Pair<Set<K>, Long>> collector) throws IOException {

    int[] nodeData = currentNode.getData();

    //Get an updated frontier that includes this identifier
    final LinkedList<DataNode> frontierHere = getFrontier(frontier, nodeData[position]);

    //If it's not the bottom, we know there's no actual count here, so can
    //skip that part
    if (position == nodeData.length - 1) {
      int[] idCopy = ArrayUtils.addAll(incrementalId, nodeData);

      if (currentNode.getCount() != 0) {

        long cumulativeCount = 0;
        for (DataNode sn : frontierHere) {
          cumulativeCount += sn.getCumulativeBelow();
        }

        Set<K> items = new HashSet<K>();
        for (int value : idCopy) {
          items.add(mapper.getValue(value));
        }

        collector.collect(Pair.of(items, cumulativeCount));
      }

      for (final QueryNode sn : currentNode.getChildren()) {
        executor.call(sn, idCopy, frontierHere, collector);
      }
    }
    //Move to the next position within this node
    else {
      supersetQuery(currentNode, incrementalId, position + 1, frontierHere, collector);
    }
  }

  /**
   * Get all the nodes at or below nodes on the current list, which include toFind
   */
  private static LinkedList<DataNode> getFrontier(LinkedList<DataNode> current, int toFind) {

    Queue<DataNode> toExplore = new LinkedList<DataNode>();
    LinkedList<DataNode> nextFrontier = new LinkedList<DataNode>();

    //First look at all the nodes on the original list.  Populate the
    //next frontier list, so we don't have to destructively modify current
    //(it is being used by other threads)
    for (DataNode sn : current) {
      int[] snData = sn.getData();

      if (sn.getAllBelow() != null) {
        if (!sn.getAllBelow().isSet(toFind)) {
          continue;
        }
      }

      int position = 0;

      boolean valid = true;
      int data;
      do {
        data = snData[position];

        if (data > toFind) {
          valid = false;
          break;
        } else if (data == toFind) {
          nextFrontier.add(sn);
          valid = false;
          break;
        }

        position++;
      } while (position != snData.length);

      if (valid) {
        Collections.addAll(toExplore, sn.getChildren());
      }
    }

    while (!toExplore.isEmpty()) {
      DataNode top = toExplore.poll();
      int[] topData = top.getData();

      if (top.getAllBelow() != null) {
        if (!top.getAllBelow().isSet(toFind)) {
          continue;
        }
      }

      int position = 0;

      boolean valid = true;
      int data;
      do {
        data = topData[position];

        if (data > toFind) {
          valid = false;
          break;
        } else if (data == toFind) {
          nextFrontier.add(top);
          valid = false;
          break;
        }

        position++;
      } while (position != topData.length);

      if (valid) {
        Collections.addAll(toExplore, top.getChildren());
      }
    }

    return nextFrontier;
  }

  //  static constructors with various defaults

  public static <T, K> BatchSubsetCountQuerier create(Iterator<T> values,
                                                      RecordExtractor<T, K> extractor,
                                                      SubsetQueryExecutor<T, K> executor,
                                                      ObjectCollector<Pair<Set<K>, Long>> output) {
    return new BatchSubsetCountQuerier<T, K>(values, extractor, new SampleFrequencyOrderedMapper<K>(), executor, output);
  }

  public static <K> BatchSubsetCountQuerier<Set<K>, K> create(Iterator<Set<K>> values,
                                                              ObjectCollector<Pair<Set<K>, Long>> output) {
    return new BatchSubsetCountQuerier<Set<K>, K>(values, new IdentityExtractor<K>(), new SampleFrequencyOrderedMapper<K>(), new SimpleSubsetExecutor<Set<K>, K>(), output);
  }

  public static <K> BatchSubsetCountQuerier<Set<K>, K> create(Iterator<Set<K>> values,
                                                              SubsetQueryExecutor<Set<K>, K> executor,
                                                              ObjectCollector<Pair<Set<K>, Long>> output) {
    return new BatchSubsetCountQuerier<Set<K>, K>(values, new IdentityExtractor<K>(), new SampleFrequencyOrderedMapper<K>(), executor, output);
  }

  public static <T, K> BatchSubsetCountQuerier create(Iterator<T> values,
                                                      RecordExtractor<T, K> extractor,
                                                      KeyMapper<K> mapper,
                                                      SubsetQueryExecutor<T, K> executor,
                                                      ObjectCollector<Pair<Set<K>, Long>> output) {
    return new BatchSubsetCountQuerier<T, K>(values, extractor, mapper, executor, output);
  }

  public static <K> BatchSubsetCountQuerier<Set<K>, K> create(Iterator<Set<K>> values,
                                                              KeyMapper<K> mapper,
                                                              SubsetQueryExecutor<Set<K>, K> executor,
                                                              ObjectCollector<Pair<Set<K>, Long>> output) {
    return new BatchSubsetCountQuerier<Set<K>, K>(values, new IdentityExtractor<K>(), mapper, executor, output);
  }

}
