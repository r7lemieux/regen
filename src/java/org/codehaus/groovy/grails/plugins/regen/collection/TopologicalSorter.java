package org.codehaus.groovy.grails.plugins.regen.collection;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Jun 3, 2010
 * Time: 9:54:29 PM
 */
public class TopologicalSorter {

static Logger LOG = Logger.getLogger(TopologicalSorter.class);

Map<String, SortNode> unsortedNodes   = new HashMap<String, SortNode>();
List<SortNode> sortedNodes            = new ArrayList<        SortNode>();

public TopologicalSorter() {
}

public List<SortNode> sort() throws Exception{

  List<SortNode> sortedNodesTail = new ArrayList<        SortNode>();

  // Peal off leaves until none come off
  boolean foundLeaf = true;
  while (!unsortedNodes.isEmpty() && foundLeaf) {
    foundLeaf = false;
    List<SortNode> unsorted = new ArrayList<SortNode>();
    for (Iterator<SortNode> ius = unsortedNodes.values().iterator(); ius.hasNext();) {
      unsorted.add(ius.next());
    }
    // Start pealing from the beginning

    for (Iterator<SortNode> ius = unsorted.iterator(); ius.hasNext();) {    
      SortNode node = ius.next();
      foundLeaf = tryRemoveFrontLeaf(node);
    }
    if (!foundLeaf) {
      // Not more loose leaf from the front side let's try the back side
      for (Iterator<SortNode> ius = unsorted.iterator(); ius.hasNext();) {
        SortNode node = ius.next();
        if (node.getBefores().isEmpty()) {
          sortedNodesTail.add(node);
          foundLeaf = true;
          for (Iterator<SortNode> ia = node.getAfters().values().iterator(); ia.hasNext();) {
            SortNode userNode = ia.next();
            userNode.removeBefore(node.name);
          }
          unsortedNodes.remove(node.name);
        }
      }
    }
  }

  if (unsortedNodes.isEmpty()) {
    // All nodes sorted
    for (int t = sortedNodesTail.size() - 1;  t >= 0; t--) {
      sortedNodes.add(sortedNodesTail.get(t));
    }
  } else {
    // Some leaves could not be removed as they point to each others
    sortedNodes = null;
    RecursiveLoopException ex = getRecursiveLoopException();
    throw ex;
  }
  if (LOG.isDebugEnabled()) {
    if (sortedNodes != null) {
      for ( SortNode node : sortedNodes) {
        LOG.debug ((node.name + Padding).substring(0,25));
      }
      for ( SortNode node : sortedNodes) {
        for (SortNode dep : node.getBefores().values()) { LOG.debug((node.getName() + "-" + Padding ).substring(0,25) + ( dep.getName()       + Padding).substring(0,25));}
        for (SortNode dep : node.getAfters() .values()) { LOG.debug(( dep.getName() +       Padding ).substring(0,25) + (node.getName() + "-" + Padding).substring(0,25));}
      }
    }
  }
  return sortedNodes;
}

private boolean tryRemoveFrontLeaf(SortNode node) {
  boolean foundLeaf = false;
  if (node.getAfters().isEmpty()) {
    foundLeaf = true;
    sortedNodes.add(node);
    for (Iterator<SortNode> ib = node.getBefores().values().iterator(); ib.hasNext();) {
      SortNode dependentNode = ib.next();
      dependentNode.removeAfter(node.name);
      tryRemoveFrontLeaf(dependentNode);
    }
    unsortedNodes.remove(node.name);
  }
  return foundLeaf;
}

public RecursiveLoopException getRecursiveLoopException() {
  StringBuffer s = new StringBuffer();

  SortNode node0 = unsortedNodes.values().iterator().next();

  s.append("Recursive dependencies detected : ");
  List<SortNode> loopNodes = null;
  try {
    loopNodes = getRecursiveDependency(node0, node0, 0);
  } catch (Exception ex) { ex.printStackTrace();}
  if (loopNodes != null)
  {
    for (int n=0; n<loopNodes.size(); n++) {
      s.append(loopNodes.get(n).getName());
      if (n < loopNodes.size() - 1) {
        s.append(" -> ");
      }
    }
  } else {
    s.append("Could not resolve loop ");
    for (SortNode node : unsortedNodes.values()) {
      s.append(node.getName());
      s.append(" : ");
      for (String beforeNodeName : node.getBefores().keySet()) {
        s.append( beforeNodeName );
        s.append(", ");
      }
    }
  }
  RecursiveLoopException ex = new RecursiveLoopException(s.toString());
  ex.setLoop(loopNodes);
  return ex;
}

public List<SortNode> getRecursiveDependency(SortNode node0, SortNode node1, int limit) {
  for (Iterator<SortNode> iun = node1.getAfters().values().iterator(); iun.hasNext() && limit < 200;) {
    SortNode afterNode = iun.next();
    if (node0 == afterNode) {
      List<SortNode> result = new ArrayList<SortNode>();
      result.add(afterNode);
      return result;
    } else {
      if (limit == 20) {
        node0 = afterNode;
      }
      List<SortNode> result = getRecursiveDependency(node0, afterNode, ++limit);
      if (result != null) {
        if (node0 == result.get(0)) {
          result.add(afterNode);
        }
        return result;
      }
    }
  }
  return null;
}

  public void addDependency(String after, String before) {
     SortNode afterNode = unsortedNodes.get(after);
     SortNode beforeNode = unsortedNodes.get(before);
     if (afterNode == null) {
       afterNode = new SortNode(after);
       unsortedNodes.put(after, afterNode);
     }
     if (beforeNode == null) {
       beforeNode = new SortNode(before);
       unsortedNodes.put(before, beforeNode);
     }
     beforeNode.addAfter(afterNode);
     afterNode.addBefore(beforeNode);
  }

public Map<String, SortNode> getUnsortedNodes() {
  return unsortedNodes;
}

public void setUnsortedNodes(Map<String, SortNode> unsortedNodes) {
  this.unsortedNodes = unsortedNodes;
}

private static String Padding = "                                                                           ";

}
