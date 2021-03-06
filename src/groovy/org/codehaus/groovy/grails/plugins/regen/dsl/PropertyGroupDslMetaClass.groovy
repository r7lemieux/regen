package org.codehaus.groovy.grails.plugins.regen.dsl

import org.apache.log4j.Logger
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

/**
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Mar 3, 2011
 * Time: 9:17:36 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertyGroupDslMetaClass extends groovy.lang.ExpandoMetaClass
{
  static Logger log = Logger.getLogger(PropertyGroupDslMetaClass.class)
  int regenParsingPass = 0
  boolean regenMode = false
  def regenClassProps = []
  def regenNewProps = []
  def regenGroups = [:]
  PropertyGroupDslMetaClass(final Class aclass)  {
    super(aclass)
    initialize()
  }
  public void initialize() {
    super.initialize()
  }
  public void reset() {
    regenNewProps = []
    regenGroups = [:]
    regenClassProps = properties.collect{p -> p.name}
    log.debug "reset regenClassProps $regenClassProps"
    log.debug "___Pass___ ${regenParsingPass} ___"
    regenMode = true;
    regenParsingPass = 0
    newPass()
  }
  void newPass() {
    regenParsingPass++
    log.debug "___Pass___ ${regenParsingPass} ___"
  }
  Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
    if (!regenMode) {
      return super.getProperty(sender, receiver, property, true, true);
    }
    if (property == 'regenNewProps') {
       if (regenParsingPass  > 0) log.debug "returning regenNewProps ${regenNewProps}"
       return regenNewProps
     } else if (property == 'regenGroups') {
      log.debug "returning regenGroups ${regenGroups}"
      return regenGroups
    } else if (property == 'regenParsingPass') {
      log.debug "returning regenParsingPass ${regenParsingPass}"
      return regenParsingPass
    }
    if (regenParsingPass == 1) {
      if (property == 'metaClass'){
        return super.getProperty(sender, receiver, property, true, true);
      } else if (regenClassProps.contains(property)) {
      return property
      } else {
        return property;
      }
    } else if (regenParsingPass == 2) {
      if (regenNewProps.contains(property)) {
         //print " newProp "
         def val
         val = regenGroups["${property}"]
         log.debug " return ${val?.class?.simpleName} $val"
         if (property == 'a1') {
           log.debug val[0]
         }
         return val
      } else if (regenClassProps.contains(property) && property != "regenNewProps" && property != "regenGroups" && property != 'metaClass') {
        log.debug "return string ${property}"
        return "'$property'"
      } else {
        log.debug "return string ${property?.class?.simpleName} ${property}"
        return "'$property'"
      }
    } else if (regenParsingPass == 3) {
      return super.getProperty(sender, receiver, property, true, true);
    }
  }
  Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
    log.debug "invokeMethod $methodName $arguments from: $sender to:$receiver $isCallToSuper $fromInsideClass"
    if (methodName == 'views' || methodName == 'rviews') {
      try {
        regenMode = true;
        reset()
        super.invokeMethod(  sender, receiver, methodName, arguments, true, true);
        newPass()
        super.invokeMethod(  sender, receiver, methodName, arguments, true, true);
        newPass()

        StringBuffer sb = new StringBuffer()
        sb.append "["
        regenGroups.entrySet().eachWithIndex{e, i ->
          if (i>0) sb.append(',')
              sb.append e.key
                sb.append (':')
                  sb.append (e.value)
        }
        sb.append(']')
        log.debug "\n" +  sb.toString()
        GroovyShell shell = new GroovyShell();
        Map mmm = [:]
        if (sb.length() > 1) {
          try {
            mmm = shell.evaluate(sb.toString())
          } catch (GroovyCastException ex) {
            def messsage = "Fail to parse Map: ${sb.toString()}"
            Log.debug message
            println message
          }
        }
        regenMode = false;
        return mmm
      } catch (MissingMethodException ex) {
        log.debug " method:$methodName $ex args:${ex.getArguments()} "
      }
    } else {
      return super.invokeMethod(sender, receiver, methodName, arguments, true, true)
    }
  }

  Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
    log.debug "MissingMethod $methodName($arguments) on $instance"
  }

  Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
    if (!regenMode) {
      return super.invokeMissingProperty(instance, propertyName, optionalValue, isGetter);
    }
    log.debug "MissingProperty $propertyName ${optionalValue?.class?.simpleName} $optionalValue "//obj:$instance optionalValue:$optionalValue isGetter $isGetter"
    if (regenParsingPass == 1) {
      regenNewProps.add(propertyName)
      log.debug " regenNewProps $regenNewProps"
    } else if (regenParsingPass == 2) {
      if (optionalValue.class == String.class) {
        regenGroups["${propertyName}"] = optionalValue[1..-2]?.tokenize(', ')
      } else {
        regenGroups["${propertyName}"] = optionalValue
      }
      log.debug "regenGroups $regenGroups"
    } else {
      log.debug "regenParsingPass $regenParsingPass val $optionalValue"
    }
    return optionalValue
  }
  Object invokeStaticMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
    log.debug "invokeStaticMissingProperty Missing $instance $propertyName"
  }
}
