package set_query_lib.runners;

import junit.framework.Assert;
import org.junit.Test;
import set_query_lib.Pair;
import set_query_lib.SampleFrequencyOrderedMapper;
import set_query_lib.collector.CollectionCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestBatchSubsetQuerier extends QueryTestCase {

  @Test
  public void testSubsetQuerier() throws IOException, InterruptedException {

    final List<Pair<Set<Integer>, Long>> counts = new ArrayList<Pair<Set<Integer>, Long>>();

    BatchSubsetCountQuerier<Set<Integer>, Integer> querier = BatchSubsetCountQuerier.create(data.iterator(),
        new SampleFrequencyOrderedMapper<Integer>(),
        new ThreadPoolQueryExecutor<Set<Integer>, Integer>(5),
        new CollectionCollector<Pair<Set<Integer>, Long>>(counts));

    querier.query(queries.iterator());
    querier.flushQueries();

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(30), 12l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 30, 40), 4l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(20, 30, 40), 4l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(30, 40, 50, 60), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(20, 30), 8l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(30, 50), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10), 11l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20), 4l)));
  }

}
