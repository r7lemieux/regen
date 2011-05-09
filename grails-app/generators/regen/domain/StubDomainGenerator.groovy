package regen.domain

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Apr 18, 2010
 * Time: 10:05:48 AM
 */

import java.lang.annotation.Annotation
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.plugins.regen.GenerationTask

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class StubDomainGenerator  {

def alter = { Map args ->
  def domainClass = args['domainClass']

  // Check if class needs simpleXml serialization
  // Set sub directory and file
  String baseDirname = System.properties['base.dir']
  String packageName = "stubs" + (domainClass.packageName?".${domainClass.packageName}":"")
  String packageDirname = packageName.replaceAll("\\.","/")
  String dirname = "${baseDirname}/src/java/${packageDirname}"
  File dir = new File(dirname)
  if (!dir.exists()) {
    dir.mkdirs();
  }


  String stubName = "${domainClass.shortName}Stub"
  String stubClassname  =  "${packageName}.${stubName}"
  String filename = "${dirname}/${stubName}.java"
  def file = new File(filename)
  if (file.exists()) {
    file.delete()
  }
  file = new File(filename)
  // Create the stub
  StringBuffer packageText = new StringBuffer();
  StringBuffer javaText = new StringBuffer();
  StringBuffer importText = new StringBuffer();
  StringBuffer bodyText = new StringBuffer();

  //if (domainClass.packageName) {
    packageText.append("package ");
    packageText.append(packageName);
    packageText.append(";\n\n");
  //}
  Class javaClass = domainClass.getClazz();
  Class superClass = javaClass.superclass;

  bodyText.append("public class ");
  bodyText.append(domainClass.shortName);
  bodyText.append("Stub ");
  if (superClass.getName() != "java.lang.Object") {
    bodyText.append("extends ")
    bodyText.append(superClass.getName());
  }
  bodyText.append(" {\n");

  domainClass.properties.each {GrailsDomainClassProperty property ->
    bodyText.append("\n")
    Annotation[] annotations = javaClass.getDeclaredField(property.getName()).getAnnotations();
    for (Annotation annotation : annotations) {
      bodyText.append(annotation.toString().replace("=,","=\"\","));
      bodyText.append("\n");
    }
    bodyText.append(property.getType().getName());
    bodyText.append(" ");
    bodyText.append(property.getName());
    bodyText.append(";\n")
  }

  bodyText.append("}");

  javaText.append(packageText.toString());
  javaText.append(importText.toString());
  javaText.append(bodyText.toString());

  file << javaText.toString()

  args.inText
}
}