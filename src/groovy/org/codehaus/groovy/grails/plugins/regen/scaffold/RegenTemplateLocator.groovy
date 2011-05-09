package org.codehaus.groovy.grails.plugins.regen.scaffold

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.codehaus.groovy.grails.plugins.regen.error.GenerationException
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.BuildSettingsHolder

/**
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Dec 20, 2010
 * Time: 10:09:11 PM
 * To change this template use File | Settings | File Templates.
 */
class RegenTemplateLocator {

    static private RegenTemplateLocator instance = new RegenTemplateLocator();
    static public RegenTemplateLocator instance() { instance }
 
	  //private static final Log LOG = LogFactory.getLog(RegenTemplateLocator.class)
    private static final String PATH_TO_VIEWS = "/WEB-INF/grails-app/views";
    //private static final String PLUGIN_PATH_TO_VIEWS = "/WEB-INF/plugins/scaffold-tags-${VERSION}/grails-app/views";

    private TemplateLocator() {}
    /*
    private findView(view, controllerUri) {
         // There needs to be a better way to do the path lookup
         def viewpaths = ["${PATH_TO_VIEWS}${controllerUri}",
                           "${PATH_TO_VIEWS}/scaffolding",
                           "${PLUGIN_PATH_TO_VIEWS}/scaffolding"]
         // println "searching for ${view} in ${viewpaths}"
       def ctx = grailsAttributes.applicationContext
       def resourceLoader = ctx.containsBean('groovyPageResourceLoader') ? ctx.groovyPageResourceLoader : ctx
         for (p in viewpaths) {
             if (view instanceof String  || view instanceof GString) {
                 def uri = "${p}/${view}"
                 def resource = resourceLoader.getResource(uri)
                 if (resource && resource.file && resource.file.exists()) {
                     // println "found-1 in ${uri} at ${resource}"
                     return uri
                 }
             } else {
                 for (v in view) {
                     def uri = "${p}/${v}"
           // println "searching for ${uri}"
                     def resource = resourceLoader.getResource(uri)
                     if (resource && resource.file && resource.file.exists()) {
                         // println "found-2 in ${uri} at ${resource}"
                         return uri
                     }
                 }
             }
         }
     // println "none found"
         return null
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
*/

}
