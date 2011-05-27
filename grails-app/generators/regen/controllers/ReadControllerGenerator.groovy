package regen.controllers

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Apr 18, 2010
 * Time: 10:32:43 AM
 */

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.BuildSettingsHolder
import org.springframework.core.io.ClassPathResource
import org.codehaus.groovy.grails.plugins.regen.error.GenerationException
import org.codehaus.groovy.grails.plugins.regen.util.TemplateLocator
import groovy.text.SimpleTemplateEngine

class ReadControllerGenerator  {

  ReadControllerGenerator() {
  }

  def alter = { args ->

    def domainClass = args.domainClass
    def viewName = args.targetName
    def genTask = args.genTask
    def templateFile = TemplateLocator.instance().getTemplateFile(genTask)
    genTask.resultFile = new FileSystemResource(
      "grails-app/controllers/${domainClass.clazz.getPackage().name.replaceAll("\\.","/")}/${domainClass.name}Controller.groovy")
    templateFile?.inputStream.getText()
 }
}
