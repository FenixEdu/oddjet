package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of a string that does not conform to the notation of template parameter names.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class IllegalTemplateParameterNameException extends Exception {

    private static final long serialVersionUID = 2735650140089148865L;

    /**
     * @param name the offending string.
     */
    public IllegalTemplateParameterNameException(String name) {
        super(name);
    }
}
