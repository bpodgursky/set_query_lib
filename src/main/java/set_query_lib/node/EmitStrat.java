package set_query_lib.node;

import java.util.Set;

public interface EmitStrat<A extends TrieNode<A>, K, O> {
  public O emit(A node, Set<K> id);
}
