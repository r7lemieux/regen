package org.codehaus.groovy.grails.plugins.regen.util;

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Jun 20, 2010
 * Time: 1:47:07 PM
 */
public class StringUtil {

  public static String capitalize(String text) {
    if (text == null) return null;
    if (text.length() == 0) return "";
    if (text.length() == 1) return text.toUpperCase();
    return text.substring(0,1).toUpperCase() + text.substring(1);
  }

  public static String unCapitalize(String text) {
    if (text == null) return null;
    if (text.length() == 0) return "";
    if (text.length() == 1) return text.toLowerCase();
    return text.substring(0,1).toLowerCase() + text.substring(1);
  }
}
