package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.rules.Assign;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.set_query_lib.KeyMapper;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Query<K, D> {
  private final List<Rule<?, D>> SIMPLIFY = RuleSet.simplifyRules();

  private final State initialState;
  protected final int[] allVariables;

  public static abstract class Factory<K, D>{
    public abstract Query<K, D> query(Expression<D> expr, KeyMapper<K> mapper);
  }

  protected Query(Expression<D> toQuery, KeyMapper<K> mapper, Set<K> variables){
    Expression<D> simplified = RuleSet.applyAll(toQuery, SIMPLIFY);

    int count = 0;
    allVariables = new int[variables.size()];
    for(K key: variables){
      allVariables[count++] = mapper.getIndex(key);
    }
    Arrays.sort(allVariables);

    initialState = getNewState(simplified, 0);
  }

  public abstract class State {

    private final Expression<D> expression;
    protected final int currentIndex;

    protected State(Expression<D> expression, int currentIndex) {
      this.currentIndex = currentIndex;
      this.expression = expression;
    }

    public State inform(int[] nodeID, KeyMapper<K> mapper){

      int queryIndex = currentIndex;
      int nodeIndex = 0;

      Map<D, Boolean> assignMap = Maps.newHashMap();
      while(nodeIndex < nodeID.length && queryIndex < allVariables.length){
        int nodeVal = nodeID[nodeIndex];
        int queryVal = allVariables[queryIndex];

        if(nodeVal > queryVal){
          assignMap.putAll(assign(mapper.getValue(queryVal), false));
          queryIndex++;
        }else if(queryVal > nodeVal){
          nodeIndex++;
        }else{
          assignMap.putAll(assign(mapper.getValue(queryVal), true));
          nodeIndex++;
          queryIndex++;
        }
      }

      Expression<D> expr = getExpression();
      if(!assignMap.isEmpty()){
        expr = RuleSet.applyAll(expr, Arrays.<Rule<?, D>>asList(new Assign<D>(assignMap)));
      }

      return getNewState(RuleSet.applyAll(expr, SIMPLIFY), queryIndex);
    }

    protected abstract Map<D, Boolean> assign(K key, boolean present);

    public Expression<D> getExpression(){
      return expression;
    }
  }

  public State getInitialState(){
    return initialState;
  }

  protected abstract State getNewState(Expression<D> expression, int currentIndex);
}

