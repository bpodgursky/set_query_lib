package set_query_lib.runners;

import jbool_expressions.And;
import jbool_expressions.Not;
import jbool_expressions.Or;
import jbool_expressions.Variable;
import org.junit.Test;
import set_query_lib.IdentityExtractor;
import set_query_lib.SampleFrequencyOrderedMapper;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestPropositionCountQuerier extends QueryTestCase {

  @Test
  public void testPropositionCountQuerier(){

    PropositionCountQuerier<Set<Integer>, Integer> querier = new PropositionCountQuerier<Set<Integer>, Integer>(
        data.iterator(),
        new IdentityExtractor<Integer>(),
        new SampleFrequencyOrderedMapper<Integer>());

    And<Integer> expr1 = And.of(
        Variable.of(10),
        Variable.of(20),
        Variable.of(30)
    );

    assertEquals(4l, querier.countMatchingRecords(expr1));

    And<Integer> expr2 = And.of(
        Variable.of(40),
        Variable.of(50)
    );

    assertEquals(2l, querier.countMatchingRecords(expr2));

    And<Integer> expr3 = And.of(
        Not.of(Variable.of(10)),
        Variable.of(20),
        Variable.of(30)
    );

    assertEquals(4l, querier.countMatchingRecords(expr3));

    Or<Integer> expr4 = Or.of(
        Variable.of(50),
        Variable.of(60)
    );

    assertEquals(6l, querier.countMatchingRecords(expr4));
  }

  @Test
  public void testFrequencies(){

    PropositionCountQuerier<Set<Integer>, Integer> querier = new PropositionCountQuerier<Set<Integer>, Integer>(
        data.iterator(),
        new IdentityExtractor<Integer>(),
        new SampleFrequencyOrderedMapper<Integer>());

    assertEquals(.5, querier.findFractionMatch(And.of(Variable.of(20), Not.of(Variable.of(10))), Variable.of(40)), .0000001);
    assertEquals(((double)4)/((double)17),
        querier.findFractionMatch(And.of(Or.of(Variable.of(10), Variable.of(20)), Not.of(Variable.of(40)))),
        .000001);
  }
}
