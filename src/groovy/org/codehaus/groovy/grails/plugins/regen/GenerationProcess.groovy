package org.codehaus.groovy.grails.plugins.regen

import org.springframework.core.io.ResourceLoader
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.FileSystemResource
import grails.util.BuildSettingsHolder
import org.apache.log4j.Logger
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Jun 6, 2010
 * Time: 7:42:38 PM
 */
public class GenerationProcess {
  // A process is an ordered set of tasks
  static Logger LOG = Logger.getLogger(GenerationProcess.class)
  Set<GenerationTask> tasks = new TreeSet<GenerationTask>(new TaskExecutionComparator<GenerationTask>());
  String instanceSuffix = 'Instance'

  def properties = [:]
  def name

  // Context
  ClassLoader classLoader
  ResourceLoader resourceLoader
  SimpleTemplateEngine engine

  // Generation Options
  boolean storeAllStepsForDebugging = true

  public GenerationProcess() {
  }

  public void generate() {
	try {
	 LOG.info "generationProcess.generate"
	
    init()
    tasks.each { GenerationTask task ->
      task.process = this
      task.init()
      try {
        task.generate()
      } catch (Exception ex) {
        if (LOG.isDebugEnabled()) ex.printStackTrace()
        LOG.info "Fail to generate process ${name ?: '' }"
      }
    }
	} catch (Exception ex) {
	  ex.printStackTrace();
	}
  }

  private init() {
    if (!classLoader) {
      classLoader = this.class.getClassLoader();
    }
    if (!engine) {
      engine = new SimpleTemplateEngine(classLoader)
    }
    def suffix = ConfigurationHolder.config?.grails?.scaffolding?.templates?.domainSuffix
    if (suffix != [:]) {
      instanceSuffix = suffix
    }
  }

  public void addTask(GenerationTask task) {
    task.process = this
    this.tasks.add(task)
  }

  String findTargetName(String targetTypeName, targetName) {
    File targetTypeDir = new File("grailsApp/${targetTypeName}")
    if (!targetTypeDir.exists() || !targetTypeDir.isDirectory()) {
      targetTypeDir = new File("grailsApp/${targetTypeName}s")
      if (!targetTypeDir.exists() || !targetTypeDir.isDirectory()) {
        targetTypeDir = new File("${targetTypeName}")
        if (!targetTypeDir.exists() ) {
          targetTypeDir = new File("src/${targetTypeName}")
          if (!targetTypeDir.exists() || !targetTypeDir.isDirectory()) {
            targetTypeDir = new File("conf/${targetTypeName}")
            if (!targetTypeDir.exists()) {
              targetTypeDir = null
            }
          }
        }
      }
    }
  if (targetTypeDir.isFile() && !targetName) {
    return targetTypeDir
  } else {
    target
  }
  }
  static defaultTargetDirectories = [domainClass:'domain']



  def getViewNames() {
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
      String templatesDirPath = "${System.properties['base.dir']}/src/templates/scaffolding"
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
