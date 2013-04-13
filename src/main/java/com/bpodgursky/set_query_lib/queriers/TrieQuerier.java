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
      Set<K> keys = extractor.getKeys(values.next());
      if(!keys.isEmpty()){
        builder.add(keys);
      }
    }

    this.root = builder.makeTrie();
    this.mapper = mapper;
  }

  public RootNode<N> getRoot(){
    return root;
  }

  protected KeyMapper<K> getMapper(){
    return mapper;
  }
}
