package org.fenixedu.oddjet.exception;


/**
 * Encapsulates a ConnectException that happens when connecting to a headless OpenOffice instance for format conversion.
 *
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 *
 */
public class OpenOfficeConnectionException extends RuntimeException {

    private static final long serialVersionUID = 218110374488459593L;

    public OpenOfficeConnectionException(Exception e) {
        super(e);
    }

}
