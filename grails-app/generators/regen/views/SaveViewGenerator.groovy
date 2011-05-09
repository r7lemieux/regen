package regen.views

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:24:37 PM
 */

class SaveViewGenerator  {

  static after = ["fillTemplate","defaultGrailsProperty"]

  def alter = { args ->
    def inText = args.inText
    def domainClass = args.domainClass
    def viewName = args.targetName

    def viewsDir = new File("${System.properties['base.dir']}/grails-app/views/${domainClass.propertyName}")
    def resultFilePath = "${viewsDir}/${viewName}.gsp"

    if (!viewsDir.exists()) {
      viewsDir.mkdirs()
    }

    File destFile = new File(resultFilePath);
    destFile.delete();
    destFile = new File(resultFilePath);
    destFile << inText;
    inText
  }
}