package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.set_query_lib.KeyMapper;
import com.bpodgursky.set_query_lib.RecordExtractor;
import com.bpodgursky.set_query_lib.node.*;

import java.util.Iterator;
import java.util.Set;

public abstract class TrieQuerier<T, K, N extends TrieNode<N>> {

  private final RootNode<N> root;
  private final KeyMapper<K> mapper;

  public TrieQuerier(Iterator<T> values,
                     RecordExtractor<T, K> extractor,
                     KeyMapper<K> mapper,
                     AddStrat<N> strat){
    TrieBuilder<K, N> builder = new TrieBuilder<K, N>(mapper, strat);

    while(values.hasNext()){
      builder.add(extractor.getKeys(values.next()));
    }

    this.root = builder.makeTrie();
    this.mapper = mapper;
  }

  protected N getRoot(){
    return root.getRoot();
  }

  protected Set<K> getValues(int[] node){
    return mapper.getValues(node);
  }

  protected int getIndex(K value){
    return mapper.getIndex(value);
  }

  protected K getValue(int node){
    return mapper.getValue(node);
  }

  protected int[] getIndices(Set<K> values){
    return mapper.getIndices(values);
  }
}
