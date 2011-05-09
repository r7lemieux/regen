package org.codehaus.groovy.grails.scaffolding

/**
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Mar 19, 2011
 * Time: 9:30:16 PM
 * To change this template use File | Settings | File Templates.
 */
class DomainClassPropertyComparator implements Comparator {

  List orderedPropertyNames = new ArrayList<Object>()

  public int compare(Object o1, Object o2) {
    return orderedPropertyNames.indexOf(o1.name) > orderedPropertyNames.index(o2.name);
  }

  public boolean equals(Object o1, Object o2) {
    return orderedPropertyNames.indexOf(o1.name) == orderedPropertyNames.index(o2.name);
  }
}
