package com.bpodgursky.set_query_lib;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetQueryLibTestCase {
  protected Set<Integer> set(Integer ... ints){
    return new HashSet<Integer>(Arrays.asList(ints));
  }
}
