package regen.views

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator
import org.codehaus.groovy.grails.plugins.regen.util.HookMerge
import org.codehaus.groovy.grails.plugins.regen.dsl.PropertyGroupDsl

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class DomainPropertiesFirstViewGenerator {

  //def static after = ['defaultGrailsProperty']

  def alter = { Map args ->
    def inText = args.inText
    def domainClass = args.domainClass
    def genTask = args.genTask

    PropertyGroupDsl propertyGroupDsl = new PropertyGroupDsl()
    def views = propertyGroupDsl.getPropertyGroups(domainClass.clazz, 'views')
    genTask.exp.viewProperties = views
    inText
  }
}
