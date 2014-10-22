package org.fenixedu.oddjet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;

import org.fenixedu.oddjet.exception.DocumentSaveException;
import org.fenixedu.oddjet.exception.OpenOfficeConnectionException;
import org.odftoolkit.simple.TextDocument;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * Printing utilities. Contains methods to print TextDocuments.
 *
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 *
 */
public class PrintUtils {
    /**
     * Connects to an headless OpenOffice process, sends it a document for convertion to pdf and
     * returns a byte array with the obtained pdf print of the document.
     *
     * @return a byte array corresponding to a pdf print of the document.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws OpenOfficeConnectionException if it fails to connect to the expected headless OpenOffice process.
     */
    public static byte[] getPDFByteArray(TextDocument doc) throws DocumentSaveException, OpenOfficeConnectionException {
        OddjetConfiguration.ConfigurationProperties config = OddjetConfiguration.getConfiguration();
        if (doc != null && config.useOpenOfficeService()) {
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(config.openOfficeHost(), config.openOfficePort());
            try {
                connection.connect();
            } catch (ConnectException e) {
                throw new OpenOfficeConnectionException(e);
            }

            DefaultDocumentFormatRegistry registry = new DefaultDocumentFormatRegistry();
            DocumentFormat outputFormat = registry.getFormatByFileExtension("pdf");
            DocumentFormat inputFormat = registry.getFormatByFileExtension("odt");
            OpenOfficeDocumentConverter converter = new OpenOfficeDocumentConverter(connection);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                doc.save(out);
            } catch (Exception e) {
                throw new DocumentSaveException(e);
            }
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            out = new ByteArrayOutputStream();

            converter.convert(in, inputFormat, out, outputFormat);

            connection.disconnect();

            return out.toByteArray();
        }
        return null;
    }
}
