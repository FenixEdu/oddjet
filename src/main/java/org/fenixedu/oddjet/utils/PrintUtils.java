package org.fenixedu.oddjet.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.fenixedu.oddjet.exception.DocumentSaveException;
import org.fenixedu.oddjet.exception.OpenOfficeConnectionException;
import org.odftoolkit.simple.TextDocument;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
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
    public static byte[] print(TextDocument doc, OpenOfficePrintingService service) throws DocumentSaveException,
            OpenOfficeConnectionException {
        if (service != null) {
            OpenOfficeConnection connection = service.getConnection();
            try {
                connection.connect();
            } catch (Exception e) {
                throw new OpenOfficeConnectionException(e);
            }

            DefaultDocumentFormatRegistry registry = new DefaultDocumentFormatRegistry();
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

            converter.convert(in, inputFormat, out, service.getOutputFormat());

            connection.disconnect();

            return out.toByteArray();
        }
        return null;
    }
}
