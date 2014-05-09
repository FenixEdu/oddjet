package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of a string that does not conform to the notation of table configuration parameters.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class IllegalTableParameterRepresentationException extends Exception {

    private static final long serialVersionUID = 8888064852558814702L;

    /**
     * @param parameter the offending string.
     */
    public IllegalTableParameterRepresentationException(String parameter) {
        super(parameter);
    }
}
