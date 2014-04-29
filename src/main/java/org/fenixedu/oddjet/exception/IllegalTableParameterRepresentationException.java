package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of a string that does not conform to the notation of table configuration parameters.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class IllegalTableParameterRepresentationException extends Exception {

    /**
     * @param parameter the offending string.
     */
    public IllegalTableParameterRepresentationException(String parameter) {
        super(parameter);
    }
}
