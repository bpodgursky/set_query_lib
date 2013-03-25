package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.ExprUtil;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.rules.Assign;
import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.bpodgursky.set_query_lib.KeyMapper;
import com.bpodgursky.set_query_lib.RecordExtractor;
import com.bpodgursky.set_query_lib.node.DataAddStrat;
import com.bpodgursky.set_query_lib.node.DataNode;

import com.bpodgursky.jbool_expressions.Expression;

import java.util.*;

public class PropositionCountQuerier<T, K> extends TrieQuerier<T, K, DataNode> {

  public PropositionCountQuerier(Iterator<T> values,
                         RecordExtractor<T, K> extractor,
                         KeyMapper<K> mapper) {
    super(values, extractor, mapper, new DataAddStrat());
  }

  public double findFractionMatch(Expression<K> prop){
    return findFractionMatch(Literal.<K>getTrue(), prop);
  }

  //  of all people who match "expr", what percent match "prop"
  public double findFractionMatch(Expression<K> filter, Expression<K> prop){
    long totalMatch = countMatchingRecords(filter);
    Expression<K> withProp = And.of(filter, prop);
    long totalWithProp = countMatchingRecords(withProp);

    return ((double) (totalWithProp)) / ((double) totalMatch);
  }

  public long countMatchingRecords(Expression<K> e){
    Expression<K> toQuery = RuleSet.simplify(e);
    Set<K> variables = ExprUtil.getVariables(toQuery);

    int count = 0;
    int[] mapped = new int[variables.size()];
    for(K key: variables){
      mapped[count++] = getIndex(key);
    }
    Arrays.sort(mapped);

    Map<K, Boolean> assignMap = new HashMap<K, Boolean>();
    List<Rule<?, K>> rules = RuleSet.simplifyRules();
    rules.add(new Assign<K>(assignMap));

    return countAt(getRoot(), e, mapped, 0, rules, assignMap);
  }

  private long countAt(DataNode node,
                       Expression<K> expression,
                       int[] allVariables, int index,
                       List<Rule<?, K>> allRules,
                       Map<K, Boolean> assignMap){

    int[] nodeID = node.getData();
    int i = 0;

    while(i < nodeID.length && index < allVariables.length){
      int nodeVal = nodeID[i];
      int queryVal = allVariables[index];

      if(nodeVal > queryVal){
        assignMap.put(getValue(queryVal), false);

        index++;
      }else if(queryVal > nodeVal){
       i++;
      }else{
        assignMap.put(getValue(queryVal), true);

        i++;
        index++;
      }
    }

    if(!assignMap.isEmpty()){
      expression = RuleSet.applyAll(expression, allRules);
      assignMap.clear();
    }

    //  expression cannot resolve true, fail
    if(expression.equals(Literal.getFalse())){
      return 0;
    }
    //  everything below here is good to go
    else if(expression.equals(Literal.getTrue())){
      return node.getCumulativeBelow();
    }

    //  if we get here, the expression should be resolved one way or another
    if(index == allVariables.length){
      throw new RuntimeException("Should have forced a literal assignment!");
    }

    long childrenCount = 0;
    for(DataNode child: node.getChildren()){
      childrenCount += countAt(child, expression, allVariables, index, allRules, assignMap);
    }

    return childrenCount;
  }
}



