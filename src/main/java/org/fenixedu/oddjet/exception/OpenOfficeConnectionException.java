package org.fenixedu.oddjet.exception;

import java.net.ConnectException;

public class OpenOfficeConnectionException extends Exception {

    public OpenOfficeConnectionException(ConnectException e) {
        super(e);
    }

}
