package org.fenixedu.oddjet.exception;

/**
 * Encapsulates exceptions that happen during the process of loading an ODF file.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class DocumentLoadException extends RuntimeException {

    private static final long serialVersionUID = -1323374446664211952L;

    public DocumentLoadException(Exception e) {
        super(e);
    }

}
