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
import org.codehaus.groovy.grails.plugins.regen.util.HookMerge
import org.codehaus.groovy.grails.plugins.regen.GenerationTask

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class SimpleXmlDomainGenerator {

public static after = ['stub']

def alter = { args ->
  String inText = args.inText
  def domainClass = args.domainClass
  // Check if class needs simpleXml serialization
  if (inText.contains("@Attribute")) {
    String packageName = "stubs" + (domainClass.packageName?".${domainClass.packageName}":"")
    String stubName = "${domainClass.shortName}Stub"
    String stubClassname  =  "${packageName}.${stubName}"

    // Add the serialization utility in the groovy class
    int closingBracketPosition = 0;
    for (int p=inText.length()-1 ; p>1 && closingBracketPosition == 0; p-- ) {
      if (inText.charAt(p) == '}') {
         closingBracketPosition = p;
      }
    }

    def newText =
    """

  //<< Simple XML Serialization

  public static ${domainClass.name} deserialize(String str) {
    ${stubClassname} stub = (${packageName}.${domainClass.name}Stub) regen.simplexml.SimpleXmlCodec.deserialize(str);
    ${domainClass.name} ${domainClass.propertyName} = new ${domainClass.name}()    """
    domainClass.properties.each { GrailsDomainClassProperty property ->
    newText += """
    ${domainClass.propertyName}.${property.name} = stub.get${property.naturalName}()"""
    }
    newText += """
    return ${domainClass.propertyName}
  }

  public String serialize() {
     ${stubClassname} stub = new ${stubClassname}();"""
      domainClass.properties.each { GrailsDomainClassProperty property ->
      newText += """
     stub.${property.name} = this.get${property.naturalName}()"""
    }
    newText +="""
     regen.simplexml.SimpleXmlCodec.serialize(stub)
  }

  //>>  Simple XML Serialization"""
    if (inText.contains("//<< Simple XML Serialization")) {
      HookMerge hookMerge = new HookMerge();
      hookMerge.mergeTemplateWithCustom(inText, newText, "groovy");
    } else {
      inText.substring(0, closingBracketPosition ) + newText + "\n}"
    }
  } else {
    inText
  }

}
}