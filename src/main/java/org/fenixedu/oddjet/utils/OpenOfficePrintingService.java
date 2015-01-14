package org.fenixedu.oddjet.utils;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.google.common.base.Preconditions;

public class OpenOfficePrintingService {

    private String host;
    private int port;
    private DocumentFormat outputFormat;
    private OpenOfficeConnection connection;

    public OpenOfficePrintingService(String host, int port, String outputFormat) {
        Preconditions.checkNotNull(host, "Invalid host.");
        Preconditions.checkArgument(!host.isEmpty(), "Invalid host.");
        Preconditions.checkArgument(port >= 0, "Invalid port.");
        Preconditions.checkArgument(port <= 65535, "Invalid port.");
        Preconditions.checkNotNull(outputFormat, "Invalid output format.");
        DocumentFormat format = new DefaultDocumentFormatRegistry().getFormatByFileExtension(outputFormat);
        Preconditions.checkArgument(format != null, "Unknown output format.");

        this.connection = new SocketOpenOfficeConnection(host, port);
        try {
            connection.connect();
            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(host + ":" + port + " service not available.", e);
        }

        this.host = host;
        this.port = port;
        this.outputFormat = format;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public DocumentFormat getOutputFormat() {
        return outputFormat;
    }

    public OpenOfficeConnection getConnection() {
        return connection;
    }

    public static boolean isValidService(String host, int port) {
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, port);
        try {
            connection.connect();
            connection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
