package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of a string that does not conform to the notation of table calls.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class IllegalTableCallRepresentationException extends Exception {

    /**
     * @param tableCall the offending string.
     */
    public IllegalTableCallRepresentationException(String tableCall) {
        super(tableCall);
    }

}
