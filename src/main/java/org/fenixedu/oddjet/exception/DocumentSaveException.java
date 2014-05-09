package org.fenixedu.oddjet.exception;

/**
 * Encapsulates exceptions that happen during the process of saving an ODF file.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class DocumentSaveException extends RuntimeException {

    private static final long serialVersionUID = -1361743814032455200L;

    public DocumentSaveException(Exception e) {
        super(e);
    }
}
