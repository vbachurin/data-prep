package org.talend.dataprep.transformation.actions.common;

/**
 * Exception to throw when an action encounter an error while compiling.
 * Might have an ActionException in the future as parent.
 */
public class ActionCompileException extends Exception {

    public ActionCompileException(String message) {
        super(message);
    }

    public ActionCompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
