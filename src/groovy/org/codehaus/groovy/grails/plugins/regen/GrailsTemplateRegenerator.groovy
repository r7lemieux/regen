package org.codehaus.groovy.grails.plugins.regen

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import grails.util.BuildSettingsHolder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.ClassPathResource


class GrailsTemplateRegenerator {

  GenerationProcess genProcess
  GrailsDomainClass domainClass
  TreeSet<String> givenTargetTypeNames = new TreeSet<String>()

  static Logger LOG = Logger.getLogger(GrailsTemplateRegenerator.class)

  GrailsTemplateRegenerator() {
  }

  void init() {
    if (!genProcess) genProcess = new GenerationProcess()
    LOG.debug "givenTargetTypeNames $givenTargetTypeNames"

    List<String> givenViewNames = new ArrayList<String>()

    if (givenTargetTypeNames.contains('all') || givenTargetTypeNames.contains('*')) {
      givenTargetTypeNames.remove('*')
      givenTargetTypeNames.remove('all')
      givenTargetTypeNames.add('controller')
      givenTargetTypeNames.add('views')
      givenTargetTypeNames.add('domain')
    }
    givenTargetTypeNames.each { String targetTypeName ->
      if (targetTypeName ==~ /view(s)?/ ) {
        givenViewNames.addAll getViewTemplateNames()
      } else if (targetTypeName.startsWith('view-') && targetTypeName.size() > 5) {
        givenViewNames << targetTypeName.substring(5)
        //println "givenViewNames $givenViewNames"
      } else {
        GenerationTask task = new GenerationTask()
        task.domainClass = domainClass
        task.targetTypeName = targetTypeName
        task.initSteps()
        if (task.genSteps) {
          genProcess.addTask(task)
        } else {
          givenViewNames << targetTypeName
        }
      }
      givenViewNames.each { String viewName ->
        GenerationTask task = new GenerationTask()
        task.domainClass = domainClass
        //task.targetTypeName = targetTypeName
        task.targetTypeName = "view"
        task.targetName = viewName
        task.initSteps()
        if (!task.genSteps) {
          task.newTargetName = true
          //givenViewNames << targetTypeName
          //task.initSteps()
          println "no steps"
        }
        if (task.genSteps) {
          genProcess.addTask(task)
        }
      }

      println "tasks ${genProcess.tasks}"
    }
  }

  void generate() {
    genProcess.generate()
  }

  def getViewTemplateNames() {
    String baseDir = System.properties['base.dir']
    def pathToSearch
    def templateTag
    def templateTags = domainClass.getPropertyOrStaticPropertyOrFieldValue("regenTemplates", Map.class)
    if (templateTags) {
      templateTag = templatetags[view]
    }
    if (!templateTag) {
      templateTag = domainClass.getPropertyOrStaticPropertyOrFieldValue("regenTemplates", String.class) ?: ''
    }
    if (templateTag) {
      LOG.debug "Looking for templateName: ${templateTag}"      // search by template tag
      pathToSearch = templateTag //.tokenize('.')[0..-1]
    } else {
      // search by path
      pathToSearch = domainClass.getPackageName() //?.tokenize('.')
    }
    collectFileNamesOnPath("${baseDir}/src/templates/scaffolding", pathToSearch)
  }

  public Set<String> collectFileNamesOnPath (String baseDir, String pathToSearch) {
    Set<String> fileNames = new TreeSet<String>()
    def pathDirs = pathToSearch.tokenize('.')
    //println "pathDirs $pathDirs"
    for (i in pathDirs.size()-1..0 ) {
      def relativePath = pathDirs[0..i].inject(''){path, dir -> "${path}/${dir}"}
      def filePath = "${baseDir}${relativePath}"
      //println "filePath ${filePath}"
      def dir = new File(filePath)
      if (dir.exists()) {
        (dir.list() as List).each{name-> if(name.endsWith('.gsp')) fileNames.add name[0..-5]}
      }
    }
    fileNames
  }

  def getViewTemplateNames0() {
    Set resources = new TreeSet()
    String basedir = System.properties['base.dir']
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
        println "grailsHome ${grailsHome}"
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