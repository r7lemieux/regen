import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator;
import org.springframework.context.ApplicationContext

import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.apache.tools.ant.taskdefs.Ant
import grails.spring.BeanBuilder
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.plugins.GrailsPluginUtils


includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << new File ("${regenPluginDir}/scripts/GrailsRegenerate.groovy")

target ('default': "Generates the requested artefacts for a specified domain class") {
	depends( checkVersion, parseArguments, packageApp )
 
  rootLoader.addURL(classesDir.toURI().toURL())
  loadApp()
  def pluginDir = GrailsPluginUtils.getPluginDirForName('regen').getURL().getPath()
  println "pluginDir ${pluginDir}"
  println "regenPluginDir ${regenPluginDir}"
  givenTargetTypeName = argsMap["params"][0]
  domainClassFullName = argsMap["params"][1]
  println "TR"
  //def gtr = new GenerationProcess()
  //println "gtr $gtr"
  def bb = new BeanBuilder()
  //bb.beans {
  //  process(GenerationProcess) {}
  //}
  println "pluginDir ${GrailsPluginUtils.getGlobalPluginsPath()}"
  println "getPluginDirForName(String pluginName) ${GrailsPluginUtils.getPluginDirForName('regen')}"
  println "pluginsHome ${pluginsHome}"
	bb.loadBeans("file:${pluginDir}/grails-app/conf/spring/resourcesSpringBeans.groovy")
  //bb.loadBeans("file:/home/richard/ws/regen/grails-app/conf/spring/resourcesSpringBeans.groovy")
  bb.getBeanDefinitions().each{println "Key ${it.key} value ${it.value}"}
  GrailsApplicationContext applicationContext = bb.createApplicationContext()
  applicationContext.getBeanDefinitionNames().each{println "applicationContext bean $it"}
  def genProcess = applicationContext.getBean('genProcess')
  println "genProcess ${genProcess}"
  def regenerator = applicationContext.getBean('regenerator')
  println "regenerator ${regenerator}"

}

