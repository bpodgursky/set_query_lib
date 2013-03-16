package set_query_lib.runners;

import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;
import set_query_lib.IdentityExtractor;
import set_query_lib.SampleFrequencyOrderedMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestSupersetQuerier extends QueryTestCase {

  @Test
  public void testSupersetQuerier() throws IOException {

    SupersetQuerier<Set<Integer>, Integer> querier = new SupersetQuerier<Set<Integer>, Integer>(
        queries.iterator(),
        new IdentityExtractor<Integer>(),
        new SampleFrequencyOrderedMapper<Integer>());

    Set<Set<Integer>> expected1 = new HashSet<Set<Integer>>();
    expected1.add(set(30));
    expected1.add(set(10, 30, 40));
    expected1.add(set(20, 30, 40));
    expected1.add(set(20, 30));
    expected1.add(set(10));
    expected1.add(set(10, 20));

    Set<Set<Integer>> expected2 = new HashSet<Set<Integer>>();
    expected2.add(set(30, 50));
    expected2.add(set(20, 30));
    expected2.add(set(30));

    Set<Set<Integer>> expected3 = new HashSet<Set<Integer>>();
    expected3.add(set(10));

    Set<Set<Integer>> expected4 = Collections.emptySet();

    //  expect all the QUERIES which are a subset of the provided data.
    //  (ex, given a person, find all groups they are a part of)

    Set<Set<Integer>> matches1 = Sets.newHashSet(querier.supersetQuery(set(10, 20, 30, 40), 100));
    Set<Set<Integer>> matches2 = Sets.newHashSet(querier.supersetQuery(set(20, 30, 50), 100));
    Set<Set<Integer>> matches3 = Sets.newHashSet(querier.supersetQuery(set(10), 100));
    Set<Set<Integer>> matches4 = Sets.newHashSet(querier.supersetQuery(set(20, 50), 100));

    Assert.assertEquals(expected1, matches1);
    Assert.assertEquals(expected2, matches2);
    Assert.assertEquals(expected3, matches3);
    Assert.assertEquals(expected4, matches4);

  }

}
