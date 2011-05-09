package regen.controllers

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Apr 18, 2010
 * Time: 9:22:22 PM
 */
class SaveControllerGenerator  {

static position = "save"

SaveControllerGenerator(){
}

def alter = { args ->
  def inText = args.inText
  def domainClass = args.domainClass
  def genTask = args.genTask
  if (inText) {
    if (!genTask.resultFile.exists()) {
      def filename = "grails-app/controllers/${domainClass.clazz.package.name.replace("\\.","/")}/${domainClass.name}Controller.groovy"
      filename.tokenize('/').inject(''){path, dir ->
        if (path) new File(path).mkdir()
        path + dir + '/'
      }
      genTask.resultFile = new FileSystemResource(filename)
    }
    genTask.resultFile.getFile().setText(inText)
    inText
  }
}

public String alter1(String inText, GrailsDomainClass domainClass, String targetName,
                    GenerationTask task) {
  if (inText) {
    def domainDir = new File("${System.properties['base.dir']}/grails-app/domain")
    def packagePath = domainClass.getPackageName().replaceAll("\\s","/")
    println "packagePath $packagePath"
    def resultFilePath = "${domainDir}/${packagePath}/${domainClass.getName()}.groovy"
    println  "resultFilePath $resultFilePath" 
    File destFile = new File(resultFilePath);
    destFile.getAbsoluteFile().delete();
    //destFile = new File(resultFilePath);
    destFile << inText;
  }
  inText
}
}
