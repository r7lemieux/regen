package regen.views

import grails.util.BuildSettingsHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.plugins.regen.error.GenerationException
import org.codehaus.groovy.grails.plugins.regen.util.TemplateLocator
import org.codehaus.groovy.grails.plugins.regen.GenerationProcess
import org.apache.log4j.Logger

class ReadViewGenerator {
  static Logger LOG = Logger.getLogger(ReadViewGenerator.class)
  
  def alter = { args ->
    def domainClass = args.domainClass
    def genTask = args.genTask
    def templateFile = TemplateLocator.instance.getTemplateFile(genTask)
    if (!templateFile || !templateFile.exists()) {
      genTask.cancel = true
      LOG.info ("Fail to find template file ${genTask.targetName} . Cancel generation of ${genTask.toString()}")
    } else {
      genTask.resultFile = new FileSystemResource("grails-app/views/${domainClass.propertyName}/${genTask.targetName}.gsp")
      templateFile?.inputStream?.getText()
    }
  }

  private getTemplateFile(String template, GenerationTask genTask) {
    if (!template) {
      throw new GenerationException("Target is null")
    }
    def application = ApplicationHolder.getApplication()
    // first check for presence of template in application
    if (genTask.process.resourceLoader && application?.warDeployed) {
      return genTask.process.resourceLoader.getResource("/WEB-INF/templates/scaffolding/${template}").inputStream.text
    }
    else {
      def templateFile = new FileSystemResource("${System.properties['base.dir']}/src/templates/scaffolding/${template}")
      if (!templateFile.exists()) {
        // template not found in application, use default template
        def grailsHome = BuildSettingsHolder.settings?.grailsHome

        if (grailsHome) {
          templateFile = new FileSystemResource("${grailsHome}/src/grails/templates/scaffolding/${template}")
        }
        else {
          templateFile = new ClassPathResource("src/grails/templates/scaffolding/${template}")
        }
      }
      return templateFile
    }
  }

}