package org.codehaus.groovy.grails.plugins.regen
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.regen.collection.TopologicalSorter
import org.codehaus.groovy.grails.plugins.regen.collection.SortNode
import org.codehaus.groovy.grails.plugins.regen.collection.RecursiveLoopException
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.regen.GrailsGeneratorClass.DefaultGeneratorNames
import org.springframework.core.io.FileSystemResource
import org.codehaus.groovy.grails.plugins.regen.error.GenerationException
import org.codehaus.groovy.grails.plugins.regen.util.StringUtil

class GenerationTask {
  static Logger LOG = Logger.getLogger(GenerationTask.class)
  static List<String> defaultGeneratorNamesList = DefaultGeneratorNames.collect {it.toString()}

  // Context
  GenerationProcess process

  // Parameters
  String name
  String targetTypeName
  String targetName
  String originalText
  String alteredText = ""
  FileSystemResource resultFile
  GrailsDomainClass domainClass
  boolean cancel = false
  boolean newTargetName = false
  String  message
  
  // Steps
  Map<String, DefaultGrailsGeneratorClass> genStepDefs = [:]
  Map<String, Object> genSteps = [:]

  // Sratch Pad
  Expando exp = new Expando();

  // Generation options
  boolean overwrite = true

  // Tool
  TopologicalSorter sorter = new TopologicalSorter()

  public void init() {
  }

  public void initSteps() {
  if (!genStepDefs) {
      LOG.debug "init generation task"
      def application = ApplicationHolder.getApplication()
      def allGenerators = application.getGeneratorClasses()
      LOG.info "generators ${allGenerators.collect{it.name}}"
      LOG.info " targetTypeName ${targetTypeName}"
      def capitalizedTargetTypeName = targetTypeName[0].toUpperCase() + targetTypeName[1..-1]
      //println "capitalizedTargetTypeName $capitalizedTargetTypeName"
      //allGenerators.each{println it.name}
      def stepGrailsClasses = allGenerators.findAll { artefact -> artefact.name.endsWith(capitalizedTargetTypeName) }
      if (stepGrailsClasses) {
        genStepDefs = sortSteps(stepGrailsClasses)

        genStepDefs.entrySet().each { Map.Entry entry ->
          genSteps.put(entry.key, entry.value.newInstance())
        }
        LOG.debug "genSteps ${genSteps.keySet()}"
      }
    }
  }

  public void generate() {
    println "generating ${domainClass.fullName} ${targetTypeName} ${targetName ?: ''} "
    this.genSteps.keySet().inject("") { text, stepName ->
      if (!cancel) {
        LOG.info("generating ${domainClass.fullName} ${targetTypeName} ${targetName ?: ''} ${stepName}")
        def genStepDef = genStepDefs[stepName]
        def genStep = genSteps[stepName]
        try {
          List templateNames = genStepDef.getPropertyOrStaticPropertyOrFieldValue('templateNames',List.class)
          LOG.debug("templateNames: $templateNames")
          if (!templateNames || templateNames.contains(this.templateName)) {
            alteredText = genStep.alter(
              inText     :alteredText,
              domainClass:domainClass,
              targetName :targetName,
              genTask    :this)
            if (!cancel && process.storeAllStepsForDebugging) {
             saveStepResults(genStepDef.name, alteredText)
            }
          }
        } catch (Exception ex) {
          if (genStepDef?.getPropertyOrStaticPropertyOrFieldValue('skipOnFailure',boolean)) {
            ex.printStackTrace()
            LOG.info("Fail step ${stepName} while generating $targetTypeName ${targetName ?: ''} for ${domainClass.name}. ${ex.getMessage()} ")
          } else {
            LOG.info("Fail to generate $targetTypeName ${targetName ?: ''} for ${domainClass.name}. Failed during step ${stepName} . ${ex.getMessage()} ")
            if (! (ex instanceof GenerationException)) {
              ex.printStackTrace()
              this.cancel = true
            }
          }
        }
      }
    }
    alteredText
  }

  def saveStepResults (String genStepDefName, String alteredText) {
    String debugDirName = "logs/regen/"
    File debugDir = new File(debugDirName)
    if (!debugDir.exists() || !debugDir.isDirectory()) {
      debugDir.mkdirs()
    }
    def extensionName = 'groovy'
    if (this.targetTypeName == 'view') {
      extensionName = 'gsp'
    }
    String filename = buildFileName([process.name, name, targetTypeName,
      targetName, domainClass.name, genStepDefName, extensionName])
    try {
      new File(debugDirName + filename).setText(alteredText)
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }

  String buildFileName (List<String>nameParts) {
    StringBuffer s = new StringBuffer();
    for (int n=0; n<nameParts.size(); n++) {
      String namePart = nameParts.get(n)
      if (namePart) {
        s.append(namePart)
        if (n < nameParts.size() -1) {
          s.append('.')
        }
      }
    }
    return s.toString()
  }
  public Map<String, DefaultGrailsGeneratorClass> sortSteps(
    List<DefaultGrailsGeneratorClass> unsortedSteps) {
    Map<String, DefaultGrailsGeneratorClass> sortedSteps   = new LinkedHashMap<String, DefaultGrailsGeneratorClass>()
    Map<String, DefaultGrailsGeneratorClass> steps         = new HashMap<String, DefaultGrailsGeneratorClass>()

    // Build default dependencies
    def stageList = []
    defaultGeneratorNamesList.each{ String stageName ->
      stageList << stageName + "-start"
      stageList << stageName + "-end"
    }
    stageList.eachWithIndex { String stageName, int index ->
      if (index) {
        addDependency(stageList[index - 1], stageName)
      }
    }

    // Build nodes
    unsortedSteps.each { gen ->
      steps.put(getGeneratorName(gen), gen)
    }

    // Extract all dependencies
    unsortedSteps.each {  gen ->
      String genName = getGeneratorName(gen)
      genStepDefs.put(genName, gen)

      // If the Generator Name ends with a stage name, add dependencies to the start and the end of that stage
      boolean hasStage = defaultGeneratorNamesList.any{ stageName ->
        if (genName.endsWith(stageName) || genName.endsWith(StringUtil.capitalize(stageName)))
        {
          sorter.addDependency(stageName + "-start", genName)
          sorter.addDependency(genName, stageName + "-end")
          true
        } else {
          false
        }
      }
      if (!hasStage && !gen.getPropertyOrStaticPropertyOrFieldValue('noStage',Boolean.class)) {
         sorter.addDependency("custom" + "-start", genName)
         sorter.addDependency(genName, "custom" + "-end")
      }

      List beforeNames = gen.getPropertyOrStaticPropertyOrFieldValue('before',List.class)
      List afterNames = gen.getPropertyOrStaticPropertyOrFieldValue('after',List.class)

      if (beforeNames) {
        beforeNames.each{ String beforeName ->
          addDependency(genName, beforeName)
        }
      } else if (!defaultGeneratorNamesList.contains(genName)) {
//        addDependency(genName, "save");
      }
      if (afterNames) {
        afterNames.each{ String afterName ->
          addDependency(afterName, genName);
        }
      } else if (!defaultGeneratorNamesList.contains(genName)) {
//        addDependency("read", genName);
      }
    }
    List<SortNode> sortedNodes = null;

    try {
      sortedNodes = sorter.sort()

    } catch (RecursiveLoopException ex) {
      println (ex.getMessage())
      //LOG.info(ex.getMessage())
    }
    sortedNodes.each { SortNode sortedNode ->   
      def sortedGenerator = steps[sortedNode.name]
      if (sortedGenerator) {
        sortedSteps.put(sortedNode.name, sortedGenerator)
      }
    }
    sortedSteps
  }

  void addDependency(before, after)
  {
    if (defaultGeneratorNamesList.contains(before)) {
      before += "-end"
    }
    if (defaultGeneratorNamesList.contains(after)) {
      after += "-start"
    }
    sorter.addDependency(before, after)
  }

  String getGeneratorName(Object gen) {
     gen.logicalPropertyName.substring(0, gen.logicalPropertyName.length() - targetTypeName.size())
  }

  String getDomainInstanceName() {
    "${domainClass.propertyName}${process.instanceSuffix}"  
  }

  String toString() {
    targetTypeName + (targetName? ' ' + targetName : '')
  }


}

