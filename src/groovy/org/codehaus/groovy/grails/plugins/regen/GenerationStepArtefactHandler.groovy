package org.codehaus.groovy.grails.plugins.regen

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Feb 11, 2010
 * Time: 8:18:59 PM
 */
class GeneratorArtefactHandler extends ArtefactHandlerAdapter{

  static final TYPE = "Generator"
  static final TYPE_CAMEL = "generator"

  GeneratorArtefactHandler() {
    super(TYPE, GrailsGeneratorClass.class, DefaultGrailsGeneratorClass.class, TYPE)
    
  }

  boolean isArtefactClass(Class clazz) {
    clazz.getName().endsWith(TYPE)
  }

  @Override
  public String getPluginName() {
    return "regen";
  }
}
