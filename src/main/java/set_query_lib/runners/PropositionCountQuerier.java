package set_query_lib.runners;

import jbool_expressions.And;
import jbool_expressions.ExprUtil;
import jbool_expressions.Literal;
import jbool_expressions.rules.Assign;
import jbool_expressions.rules.Rule;
import jbool_expressions.rules.RuleSet;
import set_query_lib.KeyMapper;
import set_query_lib.RecordExtractor;
import set_query_lib.node.DataAddStrat;
import set_query_lib.node.DataNode;
import set_query_lib.node.RootNode;

import jbool_expressions.Expression;

import java.util.*;

public class PropositionCountQuerier<T, K> {

  private final RootNode<DataNode> dataRoot;
  private final KeyMapper<K> mapper;

  public PropositionCountQuerier(Iterator<T> values,
                         RecordExtractor<T, K> extractor,
                         KeyMapper<K> mapper) {

    this.dataRoot = new RootNode<DataNode>(new DataAddStrat());
    this.mapper = mapper;

    List<Set<K>> samples = new ArrayList<Set<K>>();
    while(samples.size() < mapper.getSampleSize() && values.hasNext()){
      samples.add(extractor.getKeys(values.next()));
    }

    mapper.offerSample(samples);

    for(Set<K> sample: samples){
      dataRoot.add(mapper.getKeys(sample));
    }

    while(values.hasNext()){
      dataRoot.add(mapper.getKeys(extractor.getKeys(values.next())));
    }
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
      mapped[count++] = mapper.getIndex(key);
    }
    Arrays.sort(mapped);

    Map<K, Boolean> assignMap = new HashMap<K, Boolean>();
    List<Rule<?, K>> rules = RuleSet.simplifyRules();
    rules.add(new Assign<K>(assignMap));

    return countAt(dataRoot.getRoot(), e, mapped, 0, rules, assignMap);
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
        assignMap.put(mapper.getValue(queryVal), false);

        index++;
      }else if(queryVal > nodeVal){
       i++;
      }else{
        assignMap.put(mapper.getValue(queryVal), true);

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



