package org.codehaus.groovy.grails.plugins.regen

/**
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: May 15, 2011
 * Time: 10:42:03 PM
 * To change this template use File | Settings | File Templates.
 */
class TaskExecutionComparator implements Comparator{

  enum TargetType {domain(1), controller(2), view(3);
    int order
    TargetType(int order) { this.order = order}
  }
  int compare(Object o1, Object o2) {
    GenerationTask t1 = (GenerationTask) o1
    GenerationTask t2 = (GenerationTask) o2

    int tt1Order
    int tt2Order
    try {
      tt1Order = TargetType.valueOf(t1.targetTypeName).order
    } catch (Exception ex) {}
    try {
      tt2Order = TargetType.valueOf(t2.targetTypeName).order
    } catch (Exception ex) {}
    if ( tt1Order != tt2Order ) {
      return tt1Order - tt2Order
    } else {
      return ("" + t1.domainClass + t1.targetName > "" + t2.domainClass + t2.targetName)? 1 : -1
    }
  }
}
