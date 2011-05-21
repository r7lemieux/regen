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
import org.codehaus.groovy.grails.plugins.regen.util.TemplateLocator
import org.springframework.core.io.FileSystemResource

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 7, 2010
 * Time: 8:53:14 PM
 */
class NamingDomainGenerator {


def alter = { args ->
  String inText = args.inText
  def genTask = args.genTask
  def domainClass = args.domainClass

  int closingBracketPosition = 0;
  for (int p=inText.length()-1 ; p>1 && closingBracketPosition <=0; p-- ) {
    if (inText.charAt(p) == '}') {
       closingBracketPosition = p;
    }
  }

  def CustomRegionName = 'Regen Naming Domain'
  def sb = new StringBuilder();
  sb << """

  //<< """ << CustomRegionName <<
  """
  public String toString() {
    """
   def nameProp = domainClass.properties.find{p -> p.name == 'name'}
   if (nameProp) {
     sb << nameProp.name
   } else {
     def nameProps = domainClass.properties.find{p -> p.name ==~ /.*[Nn]ame/}
     if (nameProps) {
       nameProps.eachWithIndex { p, i ->
         if (i) sb << ' + '
         sb << p.name
       }
     } else {
       sb << 'id'
     }
   }

   sb << """
  }

  //>> """ + CustomRegionName

  if (inText.contains('//<< ' + CustomRegionName)) {
    HookMerge hookMerge = new HookMerge();
    hookMerge.mergeTemplateWithCustom(inText, sb.toString(), "groovy");
  } else {
    inText.substring(0, closingBracketPosition ) + sb + "\n}"
  }
}
}
