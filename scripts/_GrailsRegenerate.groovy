import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

domainClassFullName = null
givenTargetTypeName = null

target('default': "Generates a CRUD interface (controller + views) for a domain class") {
  depends(checkVersion, packageApp)

  typeName = "Domain Class"
  promptForName()
  generate()
}

target(generate: "Generates artefacts.") {
	
  ApplicationHolder.setApplication(grailsApp)

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

  def domainClasses = grailsApp.domainClasses
  if (!domainClasses) {
    println "No domain classes found in grails-app/domain, trying hibernate mapped classes..."
    bootstrap()
    domainClasses = grailsApp.domainClasses
  }
  if (domainClassFullName != 'all') {
    def regexFilter = domainClassFullName.replace(".", "\\.").replace("*",".*")
    domainClasses = domainClasses.findAll{it.fullName ==~ regexFilter}
  }

  if (domainClasses) {
      domainClasses.each { domainClass ->
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
  ApplicationHolder.setApplication(grailsApp);
  def genProcess = classLoader.loadClass(
    "org.codehaus.groovy.grails.plugins.regen.GenerationProcess").newInstance()
  genProcess.classLoader = classLoader
  genProcess.resourceLoader = grailsApp.getParentContext()
  def regenerator = classLoader.loadClass(
    "org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator").newInstance()
  regenerator.genProcess = genProcess

  regenerator.domainClass = domainClass
  regenerator.givenTargetTypeNames = targetTypeNames //.toList()

  regenerator.init()
  regenerator.generate()
  
  event("RegenerateEnd", [domainClass.fullName])
}
