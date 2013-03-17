package set_query_lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Hold a set of segments as an array of ints
 *
 * @author ben
 */

public class IntBitSet implements Comparable<IntBitSet>{

  private final int[] contents;

  public IntBitSet(int[] contents) {
    this.contents = contents;
  }

  public IntBitSet(List<Integer> contents){
    Collections.sort(contents);

    this.contents = new int[contents.size()];

    int index = 0;
    for(int s: contents){
      this.contents[index++]=s;
    }
  }

  public IntBitSet() {
    this.contents = new int[0];
  }

  public IntBitSet(BitSet b) {
    this.contents = new int[b.cardinality()];
    int contentsIndex = 0;
    for (int i = 0; i < b.length(); i++) {
      if (b.get(i)) {
        this.contents[contentsIndex] = i;
        contentsIndex++;
      }
    }
  }

  public int[] getContents(){
    return contents;
  }

  public int size() {
    return contents.length;
  }

  private static int countDuplicates(int[] a, int[] b){

    int i = 0;
    int j = 0;
    int dups = 0;

    while (i < a.length && j < b.length) {
      int dataArray = a[i];
      int dataAdd = b[j];

      if (dataArray == dataAdd) {
        i++;
        j++;
        dups++;

      } else if (dataArray > dataAdd) {
        j++;

      } else {
        i++;
      }
    }

    return dups;
  }

  public IntBitSet add(int[] add) {

    //1) count how many duplicates

    int dups = countDuplicates(this.contents, add);

    if (dups == add.length) {
      return this;
    }

    int[] newContents = new int[this.contents.length + (add.length - dups)];

    int i = 0;
    int j = 0;
    int contents = 0;

    while (true) {

      if (i < this.contents.length && j < add.length) {
        int dataArray = this.contents[i];
        int dataAdd = add[j];
        if (dataArray == dataAdd) {
          i++;
          j++;
          newContents[contents++] = dataArray;

        } else if (dataArray > dataAdd) {
          j++;
          newContents[contents++] = dataAdd;

        } else {
          i++;
          newContents[contents++] = dataArray;
        }
      } else if (i < this.contents.length) {
        newContents[contents++] = this.contents[i++];

      } else if (j < add.length) {
        newContents[contents++] = add[j++];

      } else {
        break;
      }
    }
    return new IntBitSet(newContents);
  }

  //if the begin and end are invalid, return null
  public IntBitSet add(int[] add, int begin, int end) {

    if (begin >= add.length || begin >= end || end > add.length) {
      throw new IllegalArgumentException("endpoints ["+begin+","+end+"] on an array length "+add.length+" illegal");
    }

    //1) count how many duplicates

    int copyEnd = Math.min(end, add.length);

    int i = 0;
    int j = begin;
    int dups = 0;

    while (i < this.contents.length && j < copyEnd) {

      int dataArray = this.contents[i];
      int dataAdd = add[j];

      if (dataArray == dataAdd) {
        i++;
        j++;
        dups++;
      } else if (dataArray > dataAdd) {
        j++;
      } else {
        i++;
      }
    }

    if(dups == add.length) {
      return this;
    }

    int[] newContents = new int[this.contents.length + (copyEnd - begin) - dups];

    i = 0;
    j = begin;
    int contents = 0;

    while (true) {

      if (i < this.contents.length && j < copyEnd) {
        int dataArray = this.contents[i];
        int dataAdd = add[j];

        if (dataArray == dataAdd) {
          i++;
          j++;
          newContents[contents++] = dataArray;

        } else if (dataArray > dataAdd) {
          j++;
          newContents[contents++] = dataAdd;

        } else {
          i++;
          newContents[contents++] = dataArray;
        }
      } else if (i < this.contents.length) {
        newContents[contents++] = this.contents[i++];

      } else if (j < copyEnd) {
        newContents[contents++] = add[j++];

      } else {
        break;
      }
    }
    return new IntBitSet(newContents);
  }

  public static int distance(IntBitSet bstA, IntBitSet bstB){

    int i = 0;
    int j = 0;

    int overlap = 0;
    while(i < bstB.contents.length && j < bstA.contents.length){

      int val = bstB.contents[i];
      int oval = bstA.contents[j];

      if(val < oval){
        i++;
      }else if(val > oval){
        j++;
      }else{
        overlap++;
        i++;
        j++;
      }
    }

    return (bstB.contents.length - overlap) + (bstA.contents.length - overlap);
  }

  /**
   * Get a copy lacking a particular number 
   */
  public IntBitSet remove(int toRemove) {
    if(contents.length == 0) return this;

    IntBitSet copy = new IntBitSet(new int[contents.length - 1]);

    int i = 0;
    int j = 0;
    while (i < contents.length) {
      if (contents[i] == toRemove) {
        i++;
      }

      if (i >= contents.length || j >= copy.contents.length) {
        break;
      }

      copy.contents[j] = contents[i];
      i++;
      j++;
    }

    //  then the number wasn't there
    if(j == i){
      return this;
    }

    return copy;
  }

  /**
   * Get a copy with a particular number added
   */
  public IntBitSet add(int toAdd){
    IntBitSet copy = new IntBitSet(new int[contents.length+1]);

    int i = 0;
    int j = 0;
    while (i < contents.length) {
      if(contents[i] == toAdd) return this;

      if (j == i && contents[i] > toAdd) {
        copy.contents[j] = toAdd;
        j++;
      }

      copy.contents[j] = contents[i];

      i++;
      j++;
    }

    if (j == i) {
      copy.contents[j] = toAdd;
    }

    return copy;
  }

  public String toString() {
    return Arrays.toString(contents);
  }

  @Override
  public boolean equals(Object b) {
    return b instanceof IntBitSet && Arrays.equals(contents, ((IntBitSet) b).contents);

  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(contents);
  }

  /**
   * Get the i-th set bit in the bitset. For example, if the bitset has {0, 0, 1, 1, 0, 1, 0}, 
   * then bitset.get(1) == 3.
   *
   * @return The index of the i-th set bit.
   */
  public int get(int i) {
    return contents[i];
  }

  public boolean isEmpty() {
    return contents.length == 0;
  }

  public final boolean isSet(int searchTarget) {
    int last;
    int middle;
    int first;

    last = contents.length - 1;
    first = 0;

    while (first <= last) {

      middle = (first + last) >>> 1; // overflow safe divide by 2

      // return search target if found
      if (contents[middle] == searchTarget) {
        return true;
      }
      // if the middle is too small
      else if (contents[middle] < searchTarget) {
        first = middle + 1;
      }
      // or too large
      else {
        last = middle - 1;
      }
    }

    // target not found
    return false;
  }

  public static IntBitSet of(int... numbers){
    Arrays.sort(numbers);
    return new IntBitSet(numbers);
  }

  public static IntBitSet of(Collection<Integer> numbers){
    return new IntBitSet(new ArrayList<Integer>(numbers));
  }

  public static IntBitSet  and(IntBitSet a, IntBitSet b){
    return and(a.contents, b.contents);
  }

  public static IntBitSet and(int[] a, int[] b){

    int dups = countDuplicates(a, b);
    int[] newContents = new int[dups];

    int i = 0;
    int j = 0;
    int newInd = 0;

    while (i < a.length && j < b.length) {
      int dataArray = a[i];
      int dataAdd = b[j];

      if (dataArray == dataAdd) {
        i++;
        j++;
        newContents[newInd++] = dataArray;

      } else if (dataArray > dataAdd) {
        j++;
      } else {
        i++;
      }
    }

    return new IntBitSet(newContents);
  }

  @Override
  public int compareTo(IntBitSet arg0) {
    if(arg0 == null) return 1;

    int i = this.contents.length-1;
    int j = arg0.contents.length-1;

    while (i>= 0 && j >= 0)	{
      int dataArray = this.contents[i];
      int dataAdd = arg0.contents[j];

      if (dataArray == dataAdd) {
        i--;
        j--;

      } else if (dataArray > dataAdd) {
        return 1;
      } else if	(dataArray < dataAdd){
        return -1;
      }
    }

    if(i >= 0) return 1;
    else if(j >= 0) return -1;
    else return 0;
  }
}
