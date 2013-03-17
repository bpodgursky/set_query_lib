package com.bpodgursky.set_query_lib.queriers;

import org.junit.Before;
import com.bpodgursky.set_query_lib.SetQueryLibTestCase;

import java.util.*;

public abstract class QueryTestCase extends SetQueryLibTestCase{

  protected List<Set<Integer>> data = new ArrayList<Set<Integer>>();
  protected List<Set<Integer>> queries = new ArrayList<Set<Integer>>();

  protected List<Integer> elementsByFrequency;


  @Before
  public void setUp() throws Exception{

    data.add(set(10));

    data.add(set(10, 20, 30));
    data.add(set(10, 20, 30));

    data.add(set(10, 30, 40));
    data.add(set(10, 30, 40));

    data.add(set(30));
    data.add(set(30));

    data.add(set(20, 30, 40));
    data.add(set(20, 30, 40));

    data.add(set(10, 20, 30, 40));
    data.add(set(10, 20, 30, 40));

    data.add(set(20, 30, 50));
    data.add(set(20, 30, 50));

    data.add(set(10, 40, 50));
    data.add(set(10, 40, 50));

    data.add(set(10, 40, 60));
    data.add(set(10, 40, 60));

    elementsByFrequency = Arrays.asList(30, 10, 40, 20, 50, 60);

    queries.add(set(20, 30));
    queries.add(set(10));
    queries.add(set(10, 30, 40));
    queries.add(set(30, 50));
    queries.add(set(10, 20));
    queries.add(set(30, 40, 50, 60));
    queries.add(set(30));
    queries.add(set(30, 40, 20));
  }
}
