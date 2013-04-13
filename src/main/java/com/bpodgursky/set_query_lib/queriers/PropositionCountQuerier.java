package com.bpodgursky.set_query_lib.queriers;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Literal;
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
    return findFractionMatch(prop, new IdentityQuery.IdentityQueryFactory<K>());
  }

  public double findFractionMatch(Expression<K> filter, Expression<K> prop){
    return findFractionMatch(filter, prop, new IdentityQuery.IdentityQueryFactory<K>());
  }

  public <D> double findFractionMatch(Expression<D> prop, Query.Factory<K, D> thing){
    return findFractionMatch(Literal.<D>getTrue(), prop, thing);
  }

  //  of all people who match "expr", what percent match "prop"
  public <D> double findFractionMatch(Expression<D> filter, Expression<D> prop, Query.Factory<K, D> factory){
    long totalMatch = countMatchingRecords(factory.query(filter, getMapper()));
    Expression<D> withProp = And.of(filter, prop);
    long totalWithProp = countMatchingRecords(factory.query(withProp, getMapper()));

    return ((double) (totalWithProp)) / ((double) totalMatch);
  }

  public long countMatchingRecords(Expression<K> expr){
    return countMatchingRecords(new IdentityQuery<K>(expr, getMapper()));
  }

  public <D> long countMatchingRecords(Query<K, D> factory){
    return countAt(getRoot().getRoot(), factory.getInitialState());
  }


  private <D> long countAt(DataNode node, Query<K, D>.State queryState){
    Query<K, D>.State newState = queryState.inform(node.getData(), getMapper());

    //  expression cannot resolve true, fail
    if(newState.getExpression().equals(Literal.getFalse())){
      return 0;
    }

    //  everything below here is good to go
    else if(newState.getExpression().equals(Literal.getTrue())){
      return node.getCumulativeBelow();
    }

    long childrenCount = 0;
    for(DataNode child: node.getChildren()){
      childrenCount += countAt(child, newState);
    }

    return childrenCount;
  }
}



