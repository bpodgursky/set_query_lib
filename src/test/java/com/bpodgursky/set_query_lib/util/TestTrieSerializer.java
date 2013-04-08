package com.bpodgursky.set_query_lib.util;

import com.bpodgursky.set_query_lib.KeyMapper;
import com.bpodgursky.set_query_lib.Pair;
import com.bpodgursky.set_query_lib.SampleFrequencyOrderedMapper;
import com.bpodgursky.set_query_lib.collector.ObjectCollector;
import com.bpodgursky.set_query_lib.node.DataAddStrat;
import com.bpodgursky.set_query_lib.node.DataNode;
import com.bpodgursky.set_query_lib.node.EmitDataNode;
import com.bpodgursky.set_query_lib.node.RootNode;
import com.bpodgursky.set_query_lib.queriers.QueryTestCase;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

public class TestTrieSerializer extends QueryTestCase {

  @Test
  public void testSerialize() throws IOException {

    KeyMapper<Integer> mapper = new SampleFrequencyOrderedMapper<Integer>();
    mapper.offerSample(data);

    RootNode<DataNode> dataNode = new RootNode<DataNode>(new DataAddStrat());
    for(Set<Integer> keys: data){
      dataNode.add(mapper.getIndices(keys));
    }

    final List<Pair<Set<Integer>, Long>> origCounts = Lists.newArrayList();
    dataNode.writeNodes(mapper, new EmitDataNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>(){
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        origCounts.add(item);
      }});

    byte[] serialized = TrieSerializer.serialize(dataNode);

    //  deserialize now

    RootNode<DataNode> deserialized = TrieSerializer.deserialize(serialized);
    final List<Pair<Set<Integer>, Long>> deserCounts = Lists.newArrayList();
    deserialized.writeNodes(mapper, new EmitDataNode<Integer>(), new ObjectCollector<Pair<Set<Integer>, Long>>(){
      public void collectInternal(Pair<Set<Integer>, Long> item) {
        deserCounts.add(item);
      }});

    assertEquals(origCounts, deserCounts);
  }
}
