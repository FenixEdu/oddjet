package org.fenixedu.oddjet.exception;

/**
 * Encapsulates an Exception that happens when converting a document to PDF.
 *
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 *
 */
public class PDFConversionException extends RuntimeException {

    private static final long serialVersionUID = 218110374488459593L;

    public PDFConversionException(Exception e) {
        super(e);
    }

}
