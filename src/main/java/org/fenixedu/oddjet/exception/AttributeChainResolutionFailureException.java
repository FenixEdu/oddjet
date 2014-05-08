package org.fenixedu.oddjet.exception;

/**
 * Signals a failure in the resolution of an attribute chain.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class AttributeChainResolutionFailureException extends Exception {

    private static final long serialVersionUID = 6111237594236674625L;

    public AttributeChainResolutionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeChainResolutionFailureException(Throwable cause) {
        super(cause);
    }

    public AttributeChainResolutionFailureException(String message) {
        super(message);
    }

}
