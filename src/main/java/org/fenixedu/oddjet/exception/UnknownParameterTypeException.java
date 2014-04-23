package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of the string representation of a possible unknown table configuration parameter.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class UnknownParameterTypeException extends Exception {

    /**
     * @param parameter the offending string.
     */
    public UnknownParameterTypeException(String parameter) {
        super(parameter);
    }

}
