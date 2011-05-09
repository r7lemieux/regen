package regen.domain

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GenerationTask

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Apr 18, 2010
 * Time: 9:22:22 PM
 */
class SaveDomainGenerator  {

static Integer ordinal = 9000
static position = "save"
SaveDomainGenerator(){
}

def alter = { args ->
  if (args.inText) {
    args.genTask.resultFile.getFile().setText(args.inText)
    args.inText
  }
}

public String alter(String inText, GrailsDomainClass domainClass, String targetName,
                    GenerationTask task) {
  if (inText) {
    def domainDir = new File("${System.properties['base.dir']}/grails-app/domain")
    def packagePath = domainClass.getPackageName().replaceAll("\\s","/")
    def resultFilePath = "${domainDir}/${packagePath}/${domainClass.getName()}.groovy"
    LOG.debug  "resultFilePath $resultFilePath"
    File destFile = new File(resultFilePath);
    destFile.getAbsoluteFile().delete();
    //destFile = new File(resultFilePath);
    destFile << inText;
  }
  inText
}
}
