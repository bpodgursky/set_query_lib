package set_query_lib.runners;

import java.io.IOException;
import java.util.*;

import com.google.common.collect.Lists;
import org.junit.Test;
import set_query_lib.KeyMapper;
import set_query_lib.Pair;
import set_query_lib.SampleFrequencyOrderedMapper;
import set_query_lib.collector.ObjectCollector;
import set_query_lib.node.*;
import junit.framework.Assert;

import static junit.framework.Assert.*;

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

		final List<Pair<Set<Integer>, Long>> counts = Lists.newArrayList();

    dataNode.writeNodes(mapper, new EmitDataNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>(){
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        counts.add(item);
      }}
    );

		assertTrue(counts.contains(Pair.of(set(), 17l)));
		assertTrue(counts.contains(Pair.of(set(30), 12l)));
		assertTrue(counts.contains(Pair.of(set(10, 30), 6l)));
		assertTrue(counts.contains(Pair.of(set(10, 30, 40), 4l)));
		assertTrue(counts.contains(Pair.of(set(10, 20, 30, 40), 2l)));
		assertTrue(counts.contains(Pair.of(set(10, 20, 30), 2l)));
		assertTrue(counts.contains(Pair.of(set(20, 30, 40), 2l)));
		assertTrue(counts.contains(Pair.of(set(20, 30, 50), 2l)));
		assertTrue(counts.contains(Pair.of(set(10, 40), 4l)));
		assertTrue(counts.contains(Pair.of(set(10, 40, 50), 2l)));
		assertTrue(counts.contains(Pair.of(set(10, 40, 60), 2l)));

    Assert.assertEquals(9, distinct);
	}

}
