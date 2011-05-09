package regen.views

import grails.util.BuildSettingsHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.plugins.regen.GenerationTask

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Feb 19, 2010
 * Time: 10:44:24 PM
 */

class GrailsOriginalViewGenerator1 {

  def genTask

  def alter = { args ->
    def inText = args.inText
    def domainClass = args.domainClass
    this.genTask = args.genTask
    def viewName = genTask.targetName
    def templateText = getTemplateText("${viewName}.gsp", genTask)
    templateText
  }

  private getTemplateText(String template, GenerationTask task) {
    def application = ApplicationHolder.getApplication()
    // first check for presence of template in application
    if (task.process.resourceLoader && application?.warDeployed) {
      return task.process.resourceLoader.getResource("/WEB-INF/templates/scaffolding/${template}").inputStream.text
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
      return templateFile.inputStream.getText()
    }
  }

  def getTemplateNames(String basedir) {
    def resources = []
    Closure filter = { it[0..-5] }
    def application = ApplicationHolder.getApplication()
    /*
    if(resourceLoader && application?.isWarDeployed()) {
      def resolver = new PathMatchingResourcePatternResolver(resourceLoader)
      try {
          resources = resolver.getResources("/WEB-INF/templates/scaffolding/*.gsp").filename.collect(filter)
      }
      catch (e) {
          return []
      }
    }
    else {
    */
        def resolver = new PathMatchingResourcePatternResolver()
        String templatesDirPath = "${basedir}/src/templates/scaffolding"
        def templatesDir = new FileSystemResource(templatesDirPath)
        if(templatesDir.exists()) {
            try {
                resources = resolver.getResources("file:$templatesDirPath/*.gsp").filename.collect(filter)
            }
            catch (e) {
                LOG.info("Error while loading views from grails-app scaffolding folder", e)
            }
        }

        def grailsHome = BuildSettingsHolder.settings?.grailsHome
        if(grailsHome) {
            try {
                def grailsHomeTemplates = resolver.getResources("file:${grailsHome}/src/grails/templates/scaffolding/*.gsp").filename.collect(filter)
                resources.addAll(grailsHomeTemplates)
            }
            catch (e) {
                // ignore
                LOG.debug("Error locating templates from GRAILS_HOME: ${e.message}", e)
            }
        }
        else {
            try {
                def templates = resolver.getResources("classpath:src/grails/templates/scaffolding/*.gsp").filename.collect(filter)
                resources.addAll(templates)
            }
            catch (e) {
                // ignore
                LOG.debug("Error locating templates from classpath: ${e.message}", e)
            }
        }
    //}
    return resources
  }
}