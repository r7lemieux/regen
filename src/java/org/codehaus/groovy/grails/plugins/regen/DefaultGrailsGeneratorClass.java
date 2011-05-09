package org.codehaus.groovy.grails.plugins.regen;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Feb 11, 2010
 * Time: 8:44:57 PM
 */
public class DefaultGrailsGeneratorClass extends AbstractGrailsClass implements GrailsGeneratorClass {

  boolean skipOnFailure = false;
  boolean writeStepResult = false;
  boolean runForGenerationOnlyNotRegeneration = false;

  public DefaultGrailsGeneratorClass(Class clazz){
    super (clazz, GENERATOR);
  }

  public boolean isWriteStepResult() { return writeStepResult;}
  public boolean getWriteStepResult() { return writeStepResult;}
  public void setWriteStepResult(boolean writeStepResult) {}

  public boolean isRunForGenerationOnlyNotRegeneration() { return runForGenerationOnlyNotRegeneration; }
  public boolean getRunForGenerationOnlyNotRegeneration() { return runForGenerationOnlyNotRegeneration; }
  public void setRunForGeneratorOnlyNotRegeneration(boolean gen){ this.runForGenerationOnlyNotRegeneration = gen;}

  public boolean getSkipOnFailure() {return skipOnFailure; }
  public boolean isSkipOnFailure() { return skipOnFailure;}
  public void setSkipOnFailure(boolean skipOnFailure) { this.skipOnFailure = skipOnFailure; }


}
