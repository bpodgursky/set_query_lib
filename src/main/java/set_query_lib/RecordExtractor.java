package set_query_lib;

import java.util.Set;

public interface RecordExtractor<T, K> {
  public Set<K> getKeys(T record);
}
