package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.set_query_lib.KeyMapper;
import com.bpodgursky.set_query_lib.node.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TrieBuilder<K, N extends TrieNode<N>> {

  private final RootNode<N> root;
  private final List<Set<K>> samples = new ArrayList<Set<K>>();
  private boolean isDoneSampling = false;

  private final KeyMapper<K> mapper;

  public TrieBuilder(KeyMapper<K> mapper,
                     AddStrat<N> addStrat) {
    this.mapper = mapper;
    this.root = new RootNode<N>(addStrat);
  }

  public void add(Set<K> item){
    if(!isDoneSampling){
      samples.add(item);
      if(samples.size() >= mapper.getSampleSize()){
        persistSamples();
      }
    }else{
      root.add(mapper.getIndices(item));
    }
  }

  public RootNode<N> makeTrie(){
    persistSamples();
    return root;
  }

  private void persistSamples(){
    mapper.offerSample(samples);
    for (Set<K> sample : samples) {
      root.add(mapper.getIndices(sample));
    }
    samples.clear();
    isDoneSampling = true;
  }
}
