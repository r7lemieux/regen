import org.codehaus.groovy.grails.plugins.regen.GeneratorArtefactHandler
import org.codehaus.groovy.grails.plugins.regen.DefaultGrailsGeneratorClass
import org.codehaus.groovy.grails.commons.DefaultGrailsControllerClass
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

class RegenGrailsPlugin {
    // the plugin version
    def version = "0.2.12"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = ['scripts/_Events.groovy'] 

    // TODO Fill in these fields
    def author = "Richard Lemieux"
    def authorEmail = "richard.lemieux@gmail.com"
    def title = "Plugin summary/headline"
    def description = '''\\
    Brief description of the plugin.
    '''
    def artefacts = [ GeneratorArtefactHandler ]
    def providedArtefacts = [
        //FirstDomainGenerator,
        //SaveDomainGenerator
    ]

    def watchedResources = [
      //"file:./src/generators/views/*Generator.groovy",
      //"file:./src/generators/**/*Generator.groovy"
      "file:./grails-app/generators/**/*Generator.groovy"

    ]
  /*
  def providedArtefacts = [
       DefaultGrailsPropertyViewGenerationStep,
       FirstViewGenerationStep,
       HookMergeViewGenerationStep,
       SaveViewGenerationStep
    ]
    */
    def onChange = { event ->
      if(application.isArtefactOfType(GeneratorArtefactHandler.TYPE, event.source)) {
         application.addArtefact(GeneratorArtefactHandler.TYPE, new DefaultGrailsGeneratorClass(event.source))
      }
      /*
      } else if(application.isArtefactOfType(DomainClassArtefactHandler.TYPE, event.source)) {

      } else if(application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {

      //} else if(event.source.name.endsWith(".gsp")) { // is source a Class or a String ?        
      } else if(event.source.endsWith(".gsp")) {
      */
      //} else {
      //  def regenSrv = applicationContext.regenConsoleService
      //  regenSrv.artefactChanged(event.source)
      //}
    }

   // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/regen"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
      for(generator in application.generatorClasses) {
        log.debug "Configuring generator $generator.fullName"
        if (generator) {
          "${generator.fullName}"(generator.clazz) { bean ->
              bean.scope = "prototype"
              bean.autowire = "byName"
          }
          process(org.codehaus.groovy.grails.plugins.regen.GenerationProcess)
        }
      }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
