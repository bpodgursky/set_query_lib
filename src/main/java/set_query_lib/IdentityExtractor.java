package set_query_lib;

import java.util.Set;

public class IdentityExtractor<A> implements RecordExtractor<Set<A>, A> {
  @Override
  public Set<A> getKeys(Set<A> record) {
    return record;
  }
}
