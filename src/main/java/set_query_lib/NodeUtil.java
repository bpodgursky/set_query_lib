package set_query_lib;

public class NodeUtil {

   /* TODO rescue or reimplement
   *
   * Find the count at or below this node of entities which have positives and do
   * not have negatives.  posIndex indicates the next positive value to satisfy,
   * negative indicates the next negative value which has not been passed yet.
   *
   * Position indicates where within a node to search--since this is a patricia
   * trie, a node can have multiple data items.
   *
  */
//  protected long countConjunctive(int position, short[] positives, short[] negatives, int pos_index, int neg_index){
//
//    //If you will not find it at or below here.
//    if(this.allBelow != null){
//      if(pos_index < positives.length &&
//          !this.allBelow.isSet(positives[pos_index])) return 0;
//    }
//
//    //If this is the last item at the node
//    boolean atEnd = position == datas.length-1;
//
//    //The target
//    int data = datas[position];
//
//    //If not all negatives have been passed
//    if(neg_index < negatives.length){
//
//      //This is the next one which may be a problem
//      short negTarget = negatives[neg_index];
//
//      //If this violates it, return nothing
//      if(data ==  negTarget ){
//        return 0;
//      }
//
//      //Otherwise, recurse back here, just with a new negative targets
//      if(negTarget < data){
//        return countConjunctive(position, positives, negatives, pos_index, neg_index+1);
//      }
//    }
//
//    //If both all the positives have been found, and there are no negatives
//    //left, return the value (you win!)
//    if(neg_index == negatives.length &&
//        pos_index == positives.length){
//      return this.cumulativeBelow;
//    }
//
//    long count = 0;
//
//    //If the next data is too large it won't ever find the target
//    if(pos_index < positives.length && data > positives[pos_index]){
//      return 0;
//    }
//    //If this is actually the target
//    else if(pos_index < positives.length && data == positives[pos_index]){
//
//      if(pos_index+1 == positives.length && position == this.datas.length-1){
//        count+=this.getLeafValue();
//      }
//
//      if(atEnd){
//        for(QueryNode sn: this.children){
//          count+=sn.countConjunctive(0, positives, negatives, pos_index+1, neg_index);
//        }
//      }else{
//        count+=countConjunctive(position+1, positives, negatives, pos_index+1, neg_index);
//      }
//    }
//    //If the positives have been found, you have no children, and the dataitems
//    //here have all been found, it's safe to ignore the rest of the negs
//    else if(this.children.length == 0 && pos_index == positives.length &&
//        position == this.datas.length-1){
//      return this.cumulativeBelow;
//    }
//    //If the current data item is less than any target
//    else{
//
//      if(atEnd){
//        for(QueryNode sn: this.children){
//          count+=sn.countConjunctive(0, positives, negatives, pos_index, neg_index);
//        }
//      }else{
//        count+=countConjunctive(position+1, positives, negatives, pos_index, neg_index);
//      }
//    }
//
//    return count;
//  }

}
