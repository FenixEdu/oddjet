package org.fenixedu.oddjet.exception;

/**
 * Signals the processing of a string that does not conform to the notation of table data source names.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class IllegalTemplateDataSourceNameException extends Exception {

    private static final long serialVersionUID = -3593761419094326948L;

    /**
     * @param name the offending string.
     */
    public IllegalTemplateDataSourceNameException(String name) {
        super(name);
    }
}
