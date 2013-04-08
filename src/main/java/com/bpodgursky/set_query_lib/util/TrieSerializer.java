package com.bpodgursky.set_query_lib.util;

import com.bpodgursky.set_query_lib.IntBitSet;
import com.bpodgursky.set_query_lib.generated.DataRoot;
import com.bpodgursky.set_query_lib.generated.Node;
import com.bpodgursky.set_query_lib.node.DataAddStrat;
import com.bpodgursky.set_query_lib.node.DataNode;
import com.bpodgursky.set_query_lib.node.RootNode;
import com.google.common.collect.Lists;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TrieSerializer {

  private static int[] toArray(List<Integer> ints){
    int[] toReturn = new int[ints.size()];
    int count = 0;
    for(Integer num: ints){
      toReturn[count++] = num;
    }
    Arrays.sort(toReturn);
    return toReturn;
  }

  private static List<Integer> toList(int[] ints){
    List<Integer> toReturn = Lists.newArrayList();
    for(int num: ints){
      toReturn.add(num);
    }
    return toReturn;
  }

  private static DataNode deserializeNode(Node node){
    List<DataNode> children = Lists.newArrayList();

    for(Node child: node.getChildren()){
      children.add(deserializeNode(child));
    }

    DataNode dn = new DataNode(toArray(node.getData()), children.toArray(new DataNode[children.size()]), node.getCumulativeCount());
    dn.setAllBelow(IntBitSet.of(node.getAllBelow()));

    return dn;
  }

  private static Node serializeNode(DataNode node){
    List<Node> children = Lists.newArrayList();
    for(DataNode child: node.getChildren()){
      children.add(serializeNode(child));
    }

    return new Node(null, node.getCumulativeBelow(), children, toList(node.getData()), toList(node.getAllBelow().getContents()));
  }

  public static byte[] serialize(RootNode<DataNode> node) throws IOException {
    DataRoot avRoot = new DataRoot(serializeNode(node.getRoot()), node.getDistinctEntries());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Encoder e = EncoderFactory.get().binaryEncoder(outputStream, null);
    DatumWriter<DataRoot> w = new SpecificDatumWriter<DataRoot>(avRoot.getSchema());
    w.write(avRoot, e);
    e.flush();

    return outputStream.toByteArray();
  }

  public static RootNode<DataNode> deserialize(byte[] node) throws IOException {
    DatumReader<DataRoot> reader = new SpecificDatumReader<DataRoot>(DataRoot.getClassSchema());
    Decoder decoder = DecoderFactory.get().binaryDecoder(node, null);
    DataRoot toReturn = new DataRoot();
    reader.read(toReturn, decoder);
    return new RootNode<DataNode>(new DataAddStrat(), deserializeNode(toReturn.getRoot()), toReturn.getDistinct());
  }
}
