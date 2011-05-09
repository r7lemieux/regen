import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator;


import org.springframework.context.ApplicationContext

import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.apache.tools.ant.taskdefs.Ant

includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << new File ("${regenPluginDir}/scripts/GrailsRegenerate.groovy")

target ('default': "Generates the requested artefacts for a specified domain class") {
	depends( checkVersion, parseArguments, packageApp )
  promptForName(type: "Domain Class")
  rootLoader.addURL(classesDir.toURI().toURL())
  loadApp()

  givenTargetTypeName = argsMap["params"][0]
  domainClassFullName = argsMap["params"][1]
	generateForOne()
}

