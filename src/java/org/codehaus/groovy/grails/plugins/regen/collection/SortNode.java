package org.codehaus.groovy.grails.plugins.regen.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * After: Richard Lemieux
 * Date: May 23, 2010
 * Time: 10:48:13 AM
 */
public class SortNode {

  String name = null;
  Map<String, SortNode> afters = new HashMap<String, SortNode>();
  Map<String, SortNode> befores = new HashMap<String, SortNode>();

public SortNode(String name) {
  this.name = name;
}

public String getName() {
  return name;
}

public void setName(String name) {
  this.name = name;
}

public Map<String, SortNode> getAfters() {
  return afters;
}

public Map<String, SortNode> getBefores() {
  return befores;
}

public boolean equals(Object other) {
  return (other instanceof SortNode) && name.equals(((SortNode)other).getName());
}
public void addAfter(SortNode sortNode) {afters.put(sortNode.getName(), sortNode);}

public void removeAfter(String after) {afters.remove(after);}

public void addBefore(SortNode sortNode) {befores.put(sortNode.getName(), sortNode);}

public void removeBefore(String before) {befores.remove(before);}

public String toString() { return getName(); }
}

