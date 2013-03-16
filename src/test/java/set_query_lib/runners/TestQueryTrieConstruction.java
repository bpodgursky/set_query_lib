package set_query_lib.runners;

import java.io.IOException;
import java.util.*;

import org.junit.Test;
import set_query_lib.KeyMapper;
import set_query_lib.Pair;
import set_query_lib.SampleFrequencyOrderedMapper;
import set_query_lib.collector.ObjectCollector;
import set_query_lib.node.*;
import junit.framework.Assert;

public class TestQueryTrieConstruction extends QueryTestCase{

  @Test
  public void testConstruction() throws IOException{

    KeyMapper<Integer> mapper = new SampleFrequencyOrderedMapper<Integer>();
    mapper.offerSample(data);

    int distinct = 0;
    RootNode<DataNode> dataNode = new RootNode<DataNode>(new DataAddStrat());
    for(Set<Integer> keys: data){
      if(dataNode.add(mapper.getKeys(keys))){
        distinct++;
      }
    }

		final List<Pair<Set<Integer>, Long>> counts = new ArrayList<Pair<Set<Integer>, Long>>();

    dataNode.writeNodes(mapper, new EmitDataNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>(){
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        counts.add(item);
      }}
    );

		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(), 17l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(30), 12l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 30), 6l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 30, 40), 4l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30, 40), 2l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 20, 30), 2l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(20, 30, 40), 2l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(20, 30, 50), 2l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 40), 4l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 40, 50), 2l)));
		Assert.assertTrue(counts.contains(new Pair<Set<Integer>, Long>(set(10, 40, 60), 2l)));

    Assert.assertEquals(9, distinct);
	}

}
