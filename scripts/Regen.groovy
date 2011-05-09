//import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
//import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
//import org.springframework.context.ApplicationContext
//import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
//import org.codehaus.groovy.grails.plugins.GrailsPlugin
//import org.apache.tools.ant.taskdefs.Ant


includeTargets << new File ("${regenPluginDir}/scripts/GrailsRegenerate.groovy")

includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target ('default': "Generates the requested artefacts for a specified domain class") {
	depends( checkVersion, parseArguments, packageApp) //, loadApp )
  promptForName(type: "Domain Class")
  rootLoader.addURL(classesDir.toURI().toURL())
  loadApp()
  def params = argsMap['params']
  if (!params || params.size != 2) {
    println '\nError: regen requires two arguments: the artefact to generate and the domain class.\n'
    return
  }
  givenTargetTypeName = params[0]
  domainClassFullName = params[1]
  if (domainClassFullName.endsWith('.iml')) {
    domainClassFullName = domainClassFullName.replacesAll(/.iml(\s)*$/, '.*')
  }
  println "regen domainClassFullName $domainClassFullName"
  
	generate()
}

