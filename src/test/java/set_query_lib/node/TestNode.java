package set_query_lib.node;

import junit.framework.Assert;
import org.junit.Test;
import set_query_lib.KeyMapper;
import set_query_lib.Pair;
import set_query_lib.SampleFrequencyOrderedMapper;
import set_query_lib.SetQueryLibTestCase;
import set_query_lib.collector.ObjectCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestNode extends SetQueryLibTestCase {

  @Test
  public void testDistinctCount() throws IOException {

    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 30));
    data.add(set(10, 40));
    data.add(set(10, 40));
    data.add(set(10));
    data.add(set(10, 50));
    data.add(set(10, 50, 60));

    KeyMapper<Integer> mapper = new SampleFrequencyOrderedMapper<Integer>();
    mapper.offerSample(data);

    RootNode<QueryNode> qnode = new RootNode<QueryNode>(new QueryAddStrat());
    for(Set<Integer> keys: data){
      qnode.add(mapper.getKeys(keys));
    }
    Assert.assertEquals(5, qnode.getDistinctEntries());

    RootNode<DataNode> dnode = new RootNode<DataNode>(new DataAddStrat());
    for(Set<Integer> keys: data){
      dnode.add(mapper.getKeys(keys));
    }
    Assert.assertEquals(5, dnode.getDistinctEntries());

    RootNode<SupersetNode> snode = new RootNode<SupersetNode>(new SupersetAddStrat());
    for(Set<Integer> keys: data){
      snode.add(mapper.getKeys(keys));
    }
    Assert.assertEquals(5, snode.getDistinctEntries());
  }

  @Test
  public void testTricky() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 20, 30));
    data.add(set(10, 15));

    List<Pair<Set<Integer>, Long>> counts = getQueries(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 15), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30), 1l)));
    Assert.assertEquals(4, counts.size());
  }

  @Test
  public void testTricky2() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 20, 30));
    data.add(set(10, 15));

    List<Pair<Set<Integer>, Long>> counts = getData(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 15), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30), 1l)));
    Assert.assertEquals(4, counts.size());
  }

  @Test
  public void testTricky3() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 20, 30));
    data.add(set(10, 20));

    List<Pair<Set<Integer>, Long>> counts = getData(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20), 2l)));
    Assert.assertEquals(3, counts.size());
  }

  @Test
  public void testTricky4() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 20, 30));
    data.add(set(10, 20));

    List<Pair<Set<Integer>, Long>> counts = getQueries(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20), 1l)));
    Assert.assertEquals(3, counts.size());
  }

  @Test
  public void testTricky5() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 30));
    data.add(set(10, 40));

    List<Pair<Set<Integer>, Long>> counts = getQueries(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10), 0l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 30), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 40), 1l)));
    Assert.assertEquals(4, counts.size());
  }

  @Test
  public void testTricky6() throws IOException {
    List<Set<Integer>> data = new ArrayList<Set<Integer>>();
    data.add(set(10, 30));
    data.add(set(10, 40));

    List<Pair<Set<Integer>, Long>> counts = getData(data);

    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10), 2l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 30), 1l)));
    Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 40), 1l)));
    Assert.assertEquals(4, counts.size());
  }



  public List<Pair<Set<Integer>, Long>> getQueries(List<Set<Integer>> data) throws IOException {
    final List<Pair<Set<Integer>, Long>> counts = new ArrayList<Pair<Set<Integer>, Long>>();
    KeyMapper<Integer> mapper = new SampleFrequencyOrderedMapper<Integer>();
    mapper.offerSample(data);
    RootNode<QueryNode> node = new RootNode<QueryNode>(new QueryAddStrat());
    for(Set<Integer> keys: data){
      if(node.add(mapper.getKeys(keys))){
      }
    }
    node.writeNodes(mapper, new EmitQueryNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>() {
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        counts.add(item);
      }
    });
    return counts;
  }

  public List<Pair<Set<Integer>, Long>> getData(List<Set<Integer>> data) throws IOException {
    final List<Pair<Set<Integer>, Long>> counts = new ArrayList<Pair<Set<Integer>, Long>>();
    KeyMapper<Integer> mapper = new SampleFrequencyOrderedMapper<Integer>();
    mapper.offerSample(data);
    RootNode<DataNode> node = new RootNode<DataNode>(new DataAddStrat());
    for(Set<Integer> keys: data){
      if(node.add(mapper.getKeys(keys))){
      }
    }

    node.writeNodes(mapper, new EmitDataNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>() {
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        counts.add(item);
      }
    }
    );
    return counts;
  }


}
