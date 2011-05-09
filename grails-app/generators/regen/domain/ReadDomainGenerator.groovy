package regen.domain

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Apr 18, 2010
 * Time: 10:32:43 AM
 */

import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.codehaus.groovy.grails.plugins.regen.GenerationTask
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.util.BuildSettingsHolder
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.regen.GenerationProcess


class ReadDomainGenerator  {
static Logger LOG = Logger.getLogger(ReadDomainGenerator.class)

  ReadDomainGenerator() {
  }

  def alter = { args ->
    def templateName = "${args.domainClass.getFullName().replaceAll("\\.","/")}.groovy"
    def templateFile = new FileSystemResource("${System.properties['base.dir']}/grails-app/domain/${templateName}")
    args.genTask.resultFile = templateFile
    if (!templateFile && templateFile.exist()) {
      args.genTask.cancel = true
      LOG.info ("Fail to find template file ${genTask.targetName} . Cancel generation of ${genTask.toString()}")
    } else {
      templateFile?.inputStream?.getText()
    }
 }

}
