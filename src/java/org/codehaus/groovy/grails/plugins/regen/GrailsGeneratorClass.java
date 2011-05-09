package org.codehaus.groovy.grails.plugins.regen;

import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.plugins.regen.GrailsTemplateRegenerator;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Feb 11, 2010
 * Time: 8:30:46 PM
 */
public interface GrailsGeneratorClass extends GrailsClass  {

public static final String GENERATOR = "Generator";

// Generator ordering key words
/*
public static final String POSITION = "position";
public static final String FIRST  = "first";
public static final String READ   = "read";
public static final String SAVE   = "save";
public static final String TEST   = "test";
public static final String LAST   = "last";
*/
public enum DefaultGeneratorNames { first, read, build, custom, merge, save, test, last };

}