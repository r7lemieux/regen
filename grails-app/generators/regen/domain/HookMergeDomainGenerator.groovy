package regen.domain

import org.codehaus.groovy.grails.plugins.regen.util.HookMerge

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class HookMergeDomainGenerator {

  static after = ['defaultGrailsProperty']
  def alter = { args ->
    def inText = args.inText
    def domainClass = args.domainClass
    def viewName = args.targetName

    def domainDir = new File("${System.properties['base.dir']}/grails-app/domain")
    def packagePath = domainClass.getPackageName().replaceAll("\\s","/")
    def prevResultFilePath = "${domainDir}/${packagePath}/${domainClass.getName()}.groovy"
    File prevResultFile = new File(prevResultFilePath);
    if (prevResultFile.exists()) {
      String prevResultText = prevResultFile.getText();
      HookMerge hookMerge = new HookMerge();
      //hookMerge.mergeTemplateWithCustom(inText, prevResultText, "groovy");
      hookMerge.mergeTemplateWithCustom(prevResultText, inText, "groovy", true);
    } else {
      inText
    }
  }
}