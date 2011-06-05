package org.codehaus.groovy.grails.plugins.regen.dsl
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Mar 3, 2011
 * Time: 9:14:10 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertyGroupDsl {
  static Logger LOG = Logger.getLogger(PropertyGroupDsl.class)

  public Map getPropertyGroups(Class clazz, String groupName) {
    def groups = readGroup(clazz, groupName)
    if (!groups) {
      def rgroups = readGroup(clazz, "r$groupName")
      groups = transpose(rgroups)
    }
    groups
  }

  public Map transpose(Map rgroups) {
    def v = [:]
    rgroups?.entrySet().each { e ->
      e.value.each { group ->
        if (group instanceof String) {
          vt = v[group]
          if (!vt) {
            vt = []
            v[group] = vt
          }
          vt.add(e.key)
        } else if (group instanceof List) {
          vt = v[group[0]]
          if (!vt) {
            vt = []
            v[group[0]] = vt
          }
          vt.add([e.key, group[1]])
        }
      }
    }
    v
  }

  public Map readGroup(Class clazz, String groupName) {
  Map groups = [:]

  String.metaClass.multiply << { List s -> [delegate, s   ] }
  String.metaClass.multiply << { Map  s -> [delegate,"\$s"] }
  
  def domainMetaClass = new PropertyGroupDslMetaClass(clazz)
  clazz.metaClass = domainMetaClass
  def c = clazz.newInstance()
  groups = InvokerHelper.invokeMethod(c, groupName, null)
  String.metaClass = null
  clazz.metaClass = null
  groups
}
 
}
