package set_query_lib.runners;

import org.junit.Test;
import set_query_lib.Pair;
import set_query_lib.SampleFrequencyOrderedMapper;
import set_query_lib.collector.CollectionCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

public class TestBatchSubsetQuerier extends QueryTestCase {

  @Test
  public void testSubsetQuerier() throws IOException, InterruptedException {

    List<Pair<Set<Integer>, Long>> counts = new ArrayList<Pair<Set<Integer>, Long>>();

    BatchSubsetCountQuerier<Set<Integer>, Integer> querier = BatchSubsetCountQuerier.create(data.iterator(),
        new SampleFrequencyOrderedMapper<Integer>(),
        new ThreadPoolQueryExecutor<Set<Integer>, Integer>(5));

    querier.query(queries.iterator());
    querier.flushQueries(new CollectionCollector<Pair<Set<Integer>, Long>>(counts));

    assertTrue(counts.contains(Pair.of(set(30), 12l)));
    assertTrue(counts.contains(Pair.of(set(10, 30, 40), 4l)));
    assertTrue(counts.contains(Pair.of(set(20, 30, 40), 4l)));
    assertTrue(counts.contains(Pair.of(set(30, 40, 50, 60), 0l)));
    assertTrue(counts.contains(Pair.of(set(20, 30), 8l)));
    assertTrue(counts.contains(Pair.of(set(30, 50), 2l)));
    assertTrue(counts.contains(Pair.of(set(10), 11l)));
    assertTrue(counts.contains(Pair.of(set(10, 20), 4l)));
  }

}
