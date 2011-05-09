import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator
import org.codehaus.groovy.grails.plugins.regen.GenerationProcess
import grails.spring.BeanBuilder
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext

domainClassFullName = null
givenTargetTypeName = null

target('default': "Generates a CRUD interface (controller + views) for a domain class") {
  depends(checkVersion, packageApp)

  typeName = "Domain Class"
  promptForName()
  generate()
}

target(generate: "Generates artefacts.") {
	
  //println "regenPluginDir ${regenPluginDir}"
  //println "main Context ${grailsApp.getMainContext()}"
  //println "parent Context ${grailsApp.getParentContext()}"
  //ConfigObject config = new ConfigSlurper().parse(classLoader.loadClass('RegenConfig'))

  ApplicationHolder.setApplication(grailsApp)

  //def bb = new BeanBuilder()
  //bb.loadBeans("file:${regenPluginDir}/grails-app/conf/spring/resourcesSpringBeans.groovy")
  //grailsApp.setMainContext(bb.createApplicationContext())
  //def genProcess = grailsApp.getMainContext().getBean('genProcess')

  println "generate domainClassFullName $domainClassFullName"
  def name
  try {
    name = (domainClassFullName.indexOf('.') > -1) ? domainClassFullName : GrailsNameUtils.getClassNameRepresentation(domainClassFullName)
  } catch (StringIndexOutOfBoundsException ex) {
    println 'Invalid class name '
    exit(0)
  }
  def domainClass = grailsApp.getDomainClass(name)
  if(domainClass) {
    generateForDomainClass(domainClass)
    event("StatusFinal", ["Done generation ${givenTargetTypeName} for domain class ${domainClass.fullName}"])
  }
  else {
    generateMany()
    event("StatusFinal", ["Done generation of ${givenTargetTypeName} for ${domainClassFullName}"])
  }
}

def generateMany() {
  //rootLoader.addURL(classesDir.toURI().toURL())

  def domainClasses = grailsApp.domainClasses
  if (!domainClasses) {
    println "No domain classes found in grails-app/domain, trying hibernate mapped classes..."
    bootstrap()
    domainClasses = grailsApp.domainClasses
  }
  if (domainClassFullName == 'all') {
    //domainClasses = domainClasses.findAll()    
  } else {
    //domainClasses.each{println "domainClass $it.fullName"}
    def regexFilter = domainClassFullName.replace(".", "\\.").replace("*",".*")
    //println "regexFilter ${regexFilter}"
    domainClasses = domainClasses.findAll{it.fullName ==~ regexFilter}
    println "domainClasses ${domainClasses}"
  }

  if (domainClasses) {
      domainClasses.each { domainClass ->
        //println "filtered domainClass ${domainClass.fullName}"
        generateForDomainClass(domainClass)
      }
  }
  else {
      event("StatusFinal", ["No domain classes found"])
  }

}

def generateForDomainClass(
  org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass domainClass
  )
{
  def targetTypeNames = new TreeSet()
  targetTypeNames.addAll(givenTargetTypeName.tokenize(","))

  //println "$targetTypeNamtes $targetTypeNames"
  ApplicationHolder.setApplication(grailsApp);
  GenerationProcess genProcess = new GenerationProcess()
  genProcess.classLoader = classLoader
  genProcess.resourceLoader = grailsApp.getParentContext()
  GrailsTemplateRegenerator regenerator = new GrailsTemplateRegenerator();
  regenerator.genProcess = genProcess

  regenerator.domainClass = domainClass
  regenerator.givenTargetTypeNames = targetTypeNames //.toList()

  regenerator.init()
  //println "to regenerator"
  regenerator.generate()
  //println "Finished generation of ${givenTargetTypeName} for ${domainClass} "
  
  event("RegenerateEnd", [domainClass.fullName])
}
