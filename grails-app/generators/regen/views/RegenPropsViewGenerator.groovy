package regen.views

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.FileSystemResource
import grails.util.BuildSettingsHolder
import org.springframework.core.io.ClassPathResource
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.persistence.Event
import java.lang.reflect.Modifier
import org.codehaus.groovy.grails.plugins.regen.util.TemplateLocator
/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Feb 20, 2010
 * Time: 6:29:56 PM
 */

class RegenPropsViewGenerator {

  def genTask
  String domainSuffix = 'Instance'
  TemplateLocator templateLocator = new TemplateLocator();

  def static after = ['read']
  
  def alter = { Map args ->

    def inText = args.inText
    def domainClass = args.domainClass
    this.genTask = args.genTask
    def viewName = genTask.targetName
    StringWriter out = new StringWriter()
    def engine = genTask.process.engine
    def suffix = ConfigurationHolder.config?.grails?.scaffolding?.templates?.domainSuffix
    if (suffix != [:]) domainSuffix = suffix 

    // define the list of properties
    def propNames = []
    def viewProperties = genTask.exp.viewProperties
    if (viewProperties) {
      propNames = viewProperties[viewName]
    } else {
      //println "domainClass.properties ${domainClass.properties}"
      propNames = domainClass.properties//.findAll {p->p.type != Set.class }
        .collect{p->p.name}   
      propNames.removeAll (Event.allEvents.toList() << 'version')
    }

    def propsAsNamed = propNames.collect { propName -> domainClass.properties.find{ dp -> propName == dp.name }}
    propsAsNamed.eachWithIndex{p,i -> if (!p) {
      println "${domainClass.name} does not have a property named ${propNames[i]}"
    }}
    def props = propsAsNamed.findAll{it}

    def comparator
    if (comparator) {
      props = Collections.sort(props, comparator)
    }
    //println "props ${props}"
    def t = engine.createTemplate(inText)
    def multiPart = domainClass.properties.find {it.type == ([] as Byte[]).class || it.type == ([] as byte[]).class}
    def packageName = domainClass.packageName ? "<%@ page import=\"${domainClass.fullName}\" %>" : ""
    def binding = [packageName: packageName,
      domainClass: domainClass,
      props: props,
      multiPart: multiPart,
      className: domainClass.shortName,
      propertyName: genTask.domainInstanceName,
      renderEditor: renderEditor,
      comparator: comparator?:org.codehaus.groovy.grails.scaffolding.DomainClassPropertyComparator.class 
    ]

    t.make(binding).writeTo(out)
    out.toString()

  }

  // a closure that uses the type to render the appropriate editor
  def renderEditor = { property ->
    def domainClass = property.domainClass
    def cp = domainClass.constrainedProperties[property.name]
    SimpleTemplateEngine engine = genTask.process.properties.propertiesEngine
    if (!engine) {
      engine = new SimpleTemplateEngine()
      genTask.process.properties.propertiesEngine = engine
    }

    Template renderEditorTemplate // = genTask.process.properties.renderEditorTemplate
    if (!renderEditorTemplate) {
       // create template once for performance
       FileSystemResource templateFile = templateLocator.findFile('renderEditor.template', domainClass, 'view');
       println "properties template file ${templateFile}"
       def templateText = templateFile?.inputStream.getText()
       renderEditorTemplate = engine.createTemplate(templateText)
       genTask.process.properties.renderEditorTemplate = renderEditorTemplate
    }

    def binding = [
      property: property,
      domainClass: domainClass,
      cp: cp,
      domainInstance: genTask.domainInstanceName
    ]
    return renderEditorTemplate.make(binding).toString()
  }

}
