package regen.controllers

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.FileSystemResource
import grails.util.BuildSettingsHolder
import org.springframework.core.io.ClassPathResource
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Aug 21, 2010
 * Time: 7:34:16 PM
 */
public class TemplateControllerGenerator {

  def alter = { args ->
    def inText = args.inText
    def domainClass = args.domainClass
    def genTask = args.genTask
    SimpleTemplateEngine engine = genTask.process.engine
    StringWriter out = new StringWriter()

    def binding = [
      packageName: domainClass.packageName,
      domainClass: domainClass,
      className: domainClass.shortName,
      propertyName: genTask.domainInstanceName,
      comparator: org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator.class]

    def t = engine.createTemplate(inText)
    t.make(binding).writeTo(out)
    out.toString()
  }
}
