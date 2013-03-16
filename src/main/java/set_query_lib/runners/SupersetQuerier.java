package set_query_lib.runners;

import set_query_lib.IntBitSet;
import set_query_lib.KeyMapper;
import set_query_lib.RecordExtractor;
import set_query_lib.node.RootNode;
import set_query_lib.node.SupersetAddStrat;
import set_query_lib.node.SupersetNode;

import java.util.*;

public class SupersetQuerier<T, K> {

  private final RootNode<SupersetNode> data;
  private final KeyMapper<K> mapper;

  private static final int SAMPLE_QUERIES = 1000;

  public SupersetQuerier(Iterator<T> values,
                         RecordExtractor<T, K> extractor,
                         KeyMapper<K> mapper) {

    this.mapper = mapper;
    this.data = new RootNode<SupersetNode>(new SupersetAddStrat());

    List<Set<K>> samples = new ArrayList<Set<K>>();
    while(samples.size() < SAMPLE_QUERIES && values.hasNext()){
      samples.add(extractor.getKeys(values.next()));
    }

    mapper.offerSample(samples);

    for(Set<K> sample: samples){
      data.add(mapper.getKeys(sample));
    }

    while(values.hasNext()){
      data.add(mapper.getKeys(extractor.getKeys(values.next())));
    }
	}

	public List<Set<K>> supersetQuery(Set<K> query, int maxMatches) {

	  // Run subset query on all children of the root.
	  IntBitSet supersetQuery = new IntBitSet(mapper.getKeys(query));

	  List<Collection<Integer>> matches = new LinkedList<Collection<Integer>>();
    matchSuperset(supersetQuery, data.getRoot(), matches, new Stack<Integer>(), maxMatches);

	  List<Set<K>> matchesReversed = new ArrayList<Set<K>>(matches.size());
	  for(Collection<Integer> match: matches){
	  	matchesReversed.add(mapper.getValues(IntBitSet.of(match).getContents()));
	  }

	  return matchesReversed;
	}

  //return true if you are done with the search and want to return
  private boolean matchSuperset(IntBitSet supersetQuery, SupersetNode curNode,
                           List<Collection<Integer>> subsetMatches, Stack<Integer> pathFromRoot, int maxMatches) {
    // Short circuit this branch of the search if the current node has an element
    // that's not in the query (i.e., it's not a subset of the query).
    for (int i: curNode.getData()) {
      if (!supersetQuery.isSet(i)) {
        return false;
      }
    }

    // The path from root is the subset represented by this node in the trie.
    for (int i : curNode.getData()) {
      pathFromRoot.push(i);
    }

    // If this node is an actual subset (rather than just an internal node of the trie),
    // add it as a match to the query.
    if(curNode.isReal()){
      List<Integer> subsetMatch = new ArrayList<Integer>(pathFromRoot);
      subsetMatches.add(subsetMatch);

      if (subsetMatches.size() >= maxMatches) {
        return true;
      }
    }

    // Recursively match on the children of this node.
    for (SupersetNode child: curNode.getChildren()) {
      if(matchSuperset(supersetQuery, child, subsetMatches, pathFromRoot, maxMatches)) return true;
    }

    // Restore the path from root before we pop back to the level above.
    for (int i = 0; i < curNode.getData().length; ++i) {
      pathFromRoot.pop();
    }

    return false;
  }
}
