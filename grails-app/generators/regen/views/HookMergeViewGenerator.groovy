package regen.views

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator
import org.codehaus.groovy.grails.plugins.regen.util.HookMerge

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class HookMergeViewGenerator {

  static after = ['defaultGrailsProperty']
  def alter = { args ->
    def inText = args.inText
    def domainClass = args.domainClass
    def viewName = args.targetName

    def viewsDir = new File("${System.properties['base.dir']}/grails-app/views/${domainClass.propertyName}")
    def prevResultFilePath = "${viewsDir}/${viewName}.gsp"
    File prevResultFile = new File(prevResultFilePath);
    if (prevResultFile.exists()) {
      String prevResultText = prevResultFile.getText();
      HookMerge hookMerge = new HookMerge();
      hookMerge.mergeTemplateWithCustom(inText, prevResultText, "gsp");
    } else {
      inText
    }
  }
}