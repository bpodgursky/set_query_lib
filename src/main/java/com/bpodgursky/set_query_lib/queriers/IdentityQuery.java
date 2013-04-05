package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.set_query_lib.KeyMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class IdentityQuery<K> extends Query<K, K> {

  public IdentityQuery(Expression<K> expr, KeyMapper<K> mapper){
    super(expr, mapper);
  }

  public static class IdentityQueryFactory<K> extends Factory<K, K> {
    @Override
    public Query<K, K> query(Expression<K> expr, KeyMapper<K> mapper) {
      return new IdentityQuery<K>(expr, mapper);
    }
  }

  @Override
  protected State getNewState(Expression<K> expression, int currentIndex) {
    return new IdentityQueryState(expression, currentIndex);
  }

  @Override
  protected Set<K> getVariables(Expression<K> query) {
    return ExprUtil.getVariables(query);
  }

  public class IdentityQueryState extends State {

    protected IdentityQueryState(Expression<K> expression, int currentIndex){
      super(expression, currentIndex);
    }

    @Override
    protected Map<K, Boolean> assign(K key, boolean present) {
      return Collections.singletonMap(key, present);
    }
  }
}
