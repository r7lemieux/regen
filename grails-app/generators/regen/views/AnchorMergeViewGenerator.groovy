package regen.views

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Mar 8, 2010
 * Time: 9:46:06 PM
 */
class AnchorMergeViewGenerator {

  static after = ['hookMerge']
  
  String prevTempltText = null;
  String prevResultText = null;
  String prevCustomText = null;
  String currTempltText = null;
  String currResultText = null;
  String currCustomText = null;


  def alter = { args  ->
    args.inText
  }

}