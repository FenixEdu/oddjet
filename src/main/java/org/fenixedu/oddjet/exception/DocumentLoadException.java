package org.fenixedu.oddjet.exception;

/**
 * Encapsulates exceptions that happen during the process of loading an ODF file.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class DocumentLoadException extends Exception {

    public DocumentLoadException(Exception e) {
        super(e);
    }

}
