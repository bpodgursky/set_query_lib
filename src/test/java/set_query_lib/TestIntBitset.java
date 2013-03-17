package set_query_lib;


import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestIntBitset {

  public TestIntBitset() throws IOException {
    super();
  }

  IntBitSet empty = new IntBitSet(new int[]{});

  @Test
  public void testEquality(){

    IntBitSet bstA = IntBitSet.of(1, 3, 4, 5);
    IntBitSet bstB = IntBitSet.of(1, 3, 4, 5);
    IntBitSet bstC = IntBitSet.of(1, 4, 5);
        
    assertEquals(bstA, bstB);
    assertFalse(bstA.equals(bstC));
    assertEquals(bstA, bstA);
    
  }

  @Test
  public void testAddArray(){

    IntBitSet bstA = new IntBitSet(new int[]{0, 3, 5});
    IntBitSet bstB = new IntBitSet(new int[]{1, 4, 6});
    IntBitSet bstC = new IntBitSet(new int[]{0, 1, 3, 4, 5, 6});
    
    assertEquals(bstA.add(bstB.getContents()), bstC);
    assertEquals(bstB.add(bstA.getContents()), bstC);
    assertEquals(bstA.add(bstA.getContents()), bstA);
    assertEquals(bstC.add(bstA.getContents()), bstC);
    
    assertEquals(bstA.add(empty.getContents()), bstA);
    assertEquals(empty.add(bstA.getContents()), bstA);
    
    assertEquals(empty.add(empty.getContents()), empty);
    
    IntBitSet bstD = new IntBitSet(new int[]{0});
    IntBitSet bstE = new IntBitSet(new int[]{1, 4});
    IntBitSet bstF = new IntBitSet(new int[]{0, 1, 4});
    IntBitSet bstG = new IntBitSet(new int[]{0, 4});
    IntBitSet bstH = new IntBitSet(new int[]{1});
    IntBitSet bstI = new IntBitSet(new int[]{8, 9});
    IntBitSet bstJ = new IntBitSet(new int[]{1, 4, 8, 9});
    
    assertEquals(bstD.add(bstE.getContents()), bstF);
    assertEquals(bstE.add(bstD.getContents()), bstF);
    assertEquals(bstG.add(bstH.getContents()), bstF);
    assertEquals(bstH.add(bstG.getContents()), bstF);
    assertEquals(bstE.add(bstI.getContents()), bstJ);
    assertEquals(bstI.add(bstE.getContents()), bstJ);
  }

  @Test
  public void testBoundedAdd(){
    
    IntBitSet bstA = new IntBitSet(new int[]{0, 3, 5});
    IntBitSet bstB = new IntBitSet(new int[]{1, 2, 5});
    IntBitSet bstC = new IntBitSet(new int[]{0, 2, 3, 5});
    IntBitSet bstD = new IntBitSet(new int[]{1, 2, 3, 5});
    IntBitSet bstE = new IntBitSet(new int[]{0, 1, 2, 3, 5});
    IntBitSet bstF = new IntBitSet(new int[]{1, 2, 4});
    IntBitSet bstG = new IntBitSet(new int[]{1, 2, 4, 5});
    
    assertEquals(bstA.add(bstB.getContents(), 1, 3), bstC);
    assertEquals(bstB.add(bstA.getContents(), 1, 3), bstD);
    assertEquals(bstA.add(bstB.getContents(), 0, 2), bstE);
    assertEquals(bstB.add(bstF.getContents(), 2, 3), bstG);
  }

  @Test
  public void testDistance(){
    
    
    IntBitSet bstA = new IntBitSet(new int[]{0, 3, 5});
    IntBitSet bstB = new IntBitSet(new int[]{1, 2, 5});
    IntBitSet bstC = new IntBitSet(new int[]{0, 2, 3, 5});
    IntBitSet bstD = new IntBitSet(new int[]{1, 2, 3, 5});
    IntBitSet bstE = new IntBitSet(new int[]{0, 1, 2, 3, 5});
    IntBitSet bstF = new IntBitSet(new int[]{1, 2, 4});
    IntBitSet bstG = new IntBitSet(new int[]{1, 2, 4, 5});
    
    assertEquals(IntBitSet.distance(bstA, bstB), 4);
    assertEquals(IntBitSet.distance(bstC, bstD), 2);
    assertEquals(IntBitSet.distance(bstA, bstA), 0);
    assertEquals(IntBitSet.distance(bstF, bstG), 1);
    assertEquals(IntBitSet.distance(bstG, bstF), 1);
    assertEquals(IntBitSet.distance(bstA, bstE), 2);
  }

  @Test
  public void testRemove(){
    
    IntBitSet bstA = new IntBitSet(new int[]{0, 3, 5});
    IntBitSet bstAminus0 = new IntBitSet(new int[]{3, 5});
    IntBitSet bstAminus3 = new IntBitSet(new int[]{0, 5});
    IntBitSet bstAminus5 = new IntBitSet(new int[]{0, 3});
    IntBitSet bstB = new IntBitSet(new int[]{3});
    
    IntBitSet bstC = new IntBitSet(new int[]{0, 2, 3, 5});
    IntBitSet bstCminus2 = new IntBitSet(new int[]{0, 3, 5});
    
    assertEquals(bstA.remove((short)0), bstAminus0);
    assertEquals(bstA.remove((short)3), bstAminus3);
    assertEquals(bstA.remove((short)5), bstAminus5);
    assertEquals(bstB.remove((short)3), empty);
    assertEquals(bstC.remove((short)2), bstCminus2);
    
    assertEquals(bstAminus0.remove((short)1), bstAminus0);
    assertEquals(bstAminus0.remove((short)4), bstAminus0);
    assertEquals(bstAminus0.remove((short)6), bstAminus0);

    assertEquals(empty.remove((short)0), empty);
  }

  @Test
  public void testAdd(){
    
    IntBitSet bstA = new IntBitSet(new int[]{0, 3, 5});
    IntBitSet bstAminus0 = new IntBitSet(new int[]{3, 5});
    IntBitSet bstAminus3 = new IntBitSet(new int[]{0, 5});
    IntBitSet bstAminus5 = new IntBitSet(new int[]{0, 3});
    IntBitSet bstB = new IntBitSet(new int[]{3});
    
    IntBitSet bstC = new IntBitSet(new int[]{0, 2, 3, 5});
    IntBitSet bstCminus2 = new IntBitSet(new int[]{0, 3, 5});
    
    assertEquals(bstAminus0.add((short)0), bstA);
    assertEquals(bstAminus3.add((short)3), bstA);
    assertEquals(bstAminus5.add((short)5), bstA);
    assertEquals(empty.add((short)3), bstB);
    assertEquals(bstCminus2.add((short)2), bstC);
    
    assertEquals(bstA.add((short)0), bstA);
    assertEquals(bstA.add((short)3), bstA);
    assertEquals(bstA.add((short)5), bstA);
  }

  @Test
  public void testClosure(){
    
    IntBitSet bstA = IntBitSet.of(0, 1, 2);
    IntBitSet bstB = IntBitSet.of(1, 2, 3);
    IntBitSet bstAB = IntBitSet.of(1, 2);
    
    assertEquals(IntBitSet.and(bstA, bstB), bstAB);
    assertEquals(IntBitSet.and(bstA, bstA), bstA);
    assertEquals(IntBitSet.and(bstA, bstAB), bstAB);
    assertEquals(IntBitSet.and(empty, empty), empty);
    assertEquals(IntBitSet.and(bstA, empty), empty);
  }
}
