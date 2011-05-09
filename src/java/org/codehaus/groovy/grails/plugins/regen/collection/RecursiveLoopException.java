package org.codehaus.groovy.grails.plugins.regen.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Richard Lemieux
 * Date: Jun 6, 2010
 * Time: 12:48:30 PM
 */
public class RecursiveLoopException extends Exception {
  List<SortNode> loop = null;

public RecursiveLoopException() {
  super();
}

public RecursiveLoopException(String message) {
  super(message);
}

public List<SortNode> getLoop() {
  return loop;
}

public void setLoop(List<SortNode> loop) {
  this.loop = loop;
}
}
