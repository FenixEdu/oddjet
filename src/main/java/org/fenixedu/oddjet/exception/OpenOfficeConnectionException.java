package org.fenixedu.oddjet.exception;

import java.net.ConnectException;

/**
 * Encapsulates a ConnectException that happens when connecting to a headless OpenOffice instance for format conversion.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class OpenOfficeConnectionException extends Exception {

    public OpenOfficeConnectionException(ConnectException e) {
        super(e);
    }

}
