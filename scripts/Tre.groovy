import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext

import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.apache.tools.ant.taskdefs.Ant
import grails.spring.BeanBuilder
import org.codehaus.groovy.grails.plugins.regen.GenerationProcess
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator

includeTargets << grailsScript("Init")
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << new File ("${grailsHome}/scripts/Bootstrap.groovy")

target(default: "The description of the script goes here!") {
  depends( checkVersion, parseArguments, packageApp )
  rootLoader.addURL(classesDir.toURI().toURL())
  loadApp()
  println "TRE"
  def gtr = new GrailsTemplateRegenerator()
  println "gtr $gtr"
  def ppp = new GenerationProcess()
  println "ppp $ppp"
  println "grailsApp ${grailsApp}"
  def bb = new BeanBuilder()
  bb.beans {
    process(GenerationProcess) {}
  }
  
  //println " grailsSettings.baseDir " + grailsSettings.baseDir
  //bb.loadBeans("file:${grailsSettings.baseDir}/grails-app/conf/spring/resource.xml")
  //bb.loadBeans("file:${grailsSettings.baseDir}/grails-app/conf/spring/resourcesSpringBeans.groovy")
  //bb.loadBeans("file:/home/richard/ws/regen/grails-app/conf/spring/resourcesSpringBeans.groovy")

  println ""
  println "bb.getBeanDefinitions() ${bb.getBeanDefinitions()}"
  bb.getBeanDefinitions().each{println "Key ${it.key} value ${it.value}"}
  GrailsApplicationContext applicationContext = bb.createApplicationContext()

  println " grailsSettings " + grailsSettings
  //applicationContext.getBeanDefinitionNames().each{println "applicationContext bean $it"}


  def process
  println "process ${process}"
  try {
  println "process ${applicationContext.getBean('process') ?: "not in application"}"
  } catch (Exception ex) {
    println ex.getMessage()
  }

  File fff = new File ("aaa.txt")
  //fff.println "GGG"

}
