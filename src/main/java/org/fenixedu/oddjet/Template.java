package org.fenixedu.oddjet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.fenixedu.oddjet.exception.DocumentLoadException;
import org.fenixedu.oddjet.exception.DocumentSaveException;
import org.fenixedu.oddjet.exception.IllegalTableCallRepresentationException;
import org.fenixedu.oddjet.exception.IllegalTemplateParameterNameException;
import org.fenixedu.oddjet.exception.OpenOfficeConnectionException;
import org.fenixedu.oddjet.table.TableCall;
import org.fenixedu.oddjet.table.TableConfiguration;
import org.fenixedu.oddjet.table.TableConfiguration.ContentDirection;
import org.fenixedu.oddjet.table.TableConfiguration.ContentStructure;
import org.fenixedu.oddjet.table.TableConfiguration.LastBorderSourceSection;
import org.fenixedu.oddjet.table.TableCoordinate;
import org.fenixedu.oddjet.table.TableData;
import org.odftoolkit.odfdom.converter.core.utils.IOUtils;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * Contains a template file along with the teplate's data and locale, allowing creating instances of the original template
 * document with the contained data and locale, printing them to pdf and saving the results.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class Template {

    /** The bytes of the template document file. */
    private byte[] bytes;
    /** The locale of the template. */
    private Locale locale;
    /** Map of template data parameters. */
    final private Map<String, Object> dataParameters = new HashMap<String, Object>();
    /** Map of template table data sources. */
    final private Map<String, TableData> tableDataSources = new HashMap<String, TableData>();

    /** The regex string to match parameter attribute access. */
    private static final String ATTRIBUTE_ACCESS_REGEX = "\\.";
    private static final Logger logger = LoggerFactory.getLogger(Template.class);

    /**
     * Constructs a Template reading a template file from a given file path and with a given locale.
     * 
     * @param filePath the path to the template file.
     * @param locale the template's locale.
     * @throws SecurityException if read access to the file is denied.
     * @throws IOException if the file could not be read, possibly because it does not exist
     *             (a <code>FileNotFoundException</code>).
     */
    public Template(String filePath, Locale locale) throws SecurityException, IOException {
        File file = new File(filePath);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(file), ostream);
        this.bytes = ostream.toByteArray();
        this.locale = locale;
    }

    /**
     * Constructs a Template reading a template file from a given file path and with the default locale.
     * 
     * @param filePath the path to the template file.
     * @throws SecurityException if read access to the file is denied.
     * @throws IOException if the file could not be read, possibly because it does not exist
     *             (a <code>FileNotFoundException</code>).
     */
    public Template(String filePath) throws SecurityException, IOException {
        this(filePath, Locale.getDefault());
    }

    /**
     * Constructs a Template reading a given template file and with a given locale.
     * 
     * @param file the template file.
     * @param locale the template's locale.
     * @throws SecurityException if read access to the file is denied.
     * @throws IOException if the file could not be read, possibly because it does not exist
     *             (a <code>FileNotFoundException</code>).
     */
    public Template(File file, Locale locale) throws SecurityException, IOException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(file), ostream);
        this.bytes = ostream.toByteArray();
        this.locale = locale;
    }

    /**
     * Constructs a Template reading a given template file and with the default locale.
     * 
     * @param file the template file.
     * @throws SecurityException if read access to the file is denied.
     * @throws IOException if the file could not be read, possibly because it does not exist
     *             (a <code>FileNotFoundException</code>).
     */
    public Template(File file) throws SecurityException, IOException {
        this(file, Locale.getDefault());
    }

    /**
     * Constructs a Template reading the template file from a given InputStream and with a given locale.
     * 
     * @param fileStream the stream to read the template file.
     * @param locale the template's locale.
     * @throws IOException if the file could not be read from the string.
     */
    public Template(InputStream fileStream, Locale locale) throws IOException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        IOUtils.copy(fileStream, ostream);
        this.bytes = ostream.toByteArray();
        this.locale = locale;
    }

    /**
     * Constructs a Template reading the template file from a given InputStream and with the default locale.
     * 
     * @param fileStream the stream to read the template file.
     * @throws IOException if the file could not be read from the string.
     */
    public Template(InputStream fileStream) throws IOException {
        this(fileStream, Locale.getDefault());
    }

    /**
     * @return the map of template data parameters.
     */
    public Map<String, Object> getParameters() {
        return dataParameters;
    }

    /**
     * Adds or replaces a template data parameter.
     * 
     * @param name the name of the data parameter.
     * @param value the data object for this parameter.
     * @throws IllegalTemplateParameterNameException if the supplied name contains the attribute access operator ".".
     */
    public void addParameter(String name, Object value) throws IllegalTemplateParameterNameException {
        if (Pattern.compile(ATTRIBUTE_ACCESS_REGEX).matcher(name).find()) {
            throw new IllegalTemplateParameterNameException(name);
        } else {
            this.dataParameters.put(name, value);
        }
    }

    /** Removes all template data parameters. */
    public void clearParameters() {
        this.dataParameters.clear();
    }

    /**
     * Removes the template data parameter with the given name.
     * 
     * @param name the name of the data parameter that is to be removed.
     */
    public void removeParameter(String name) {
        this.dataParameters.remove(name);
    }

    /**
     * Adds or replaces a template table's data source.
     * 
     * @param name the name of the table to contain this data.
     * @param value the object containing the table data.
     */
    public void addTableDataSource(String name, TableData value) {
        this.tableDataSources.put(name, value);
    }

    /** Removes all template table data sources. */
    public void clearTableDataSources() {
        this.tableDataSources.clear();
    }

    /**
     * Removes the template table's data source for the table with the given name.
     * 
     * @param name the name of the table whose data source is to be removed.
     */
    public void removeTableDataSource(String name) {
        this.tableDataSources.remove(name);
    }

    /**
     * @return the map of template table data sources.
     */
    public Map<String, TableData> getTableDataSources() {
        return tableDataSources;
    }

    /**
     * @return the template locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param locale the locale to be set.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Loads the template document from the stored document bytes, fills its variable content with the added data and returns it.
     * 
     * @return the TextDocument object corresponding to an instance of this template.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public TextDocument getInstance() throws DocumentLoadException {
        TextDocument document;
        try {
            document = TextDocument.loadDocument(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new DocumentLoadException(e);
        }
        fillUserFields(document, getParameters(), getLocale());
        fillTables(document, getTableDataSources(), getLocale());
        return document;
    }

    /**
     * Instantiates the template through {@link #getInstance()} and attempts to save it as a file to the given path.
     * 
     * @param path the path to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to the given path.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public void saveInstance(String path) throws DocumentSaveException, DocumentLoadException {
        TextDocument document = getInstance();
        try {
            document.save(path);
        } catch (Exception e) {
            throw new DocumentSaveException(e);
        }
        document.close();
    }

    /**
     * Instantiates the template through {@link #getInstance()} and attempts to save it to the given file.
     * 
     * @param file the file to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to the given file.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public void saveInstance(File file) throws DocumentLoadException, DocumentSaveException {
        TextDocument document = getInstance();

        try {
            document.save(file);
        } catch (Exception e) {
            throw new DocumentSaveException(e);
        }
        document.close();
    }

    /**
     * Instantiates the template through {@link #getInstance()} and attempts to save it to the given OutputStream.
     * 
     * @param stream the OutputStream to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to the given OutputStream.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public void saveInstance(OutputStream stream) throws DocumentLoadException, DocumentSaveException {
        TextDocument document = getInstance();
        try {
            document.save(stream);
        } catch (Exception e) {
            throw new DocumentSaveException(e);
        }
        document.close();
    }

    /**
     * Saves an instance of the template through {@link #saveInstance(OutputStream)} into a byte array and returns it.
     * 
     * @return a byte array corresponding to an instance of this template.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public byte[] getInstanceByteArray() throws DocumentLoadException, DocumentSaveException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        saveInstance(ostream);
        return ostream.toByteArray();
    }

    /**
     * Connects to an headless OpenOffice process, sends it an instance byte array, obtained through
     * {@link #getInstanceByteArray()} for convertion to pdf and returns a byte array with the obtained pdf print of the instance.
     * 
     * @return a byte array corresponding to a pdf print of an instance of this template.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     * @throws OpenOfficeConnectionException if it fails to connect to the expected headless OpenOffice process.
     */
    public byte[] getInstancePDFByteArray() throws DocumentLoadException, DocumentSaveException, OpenOfficeConnectionException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        ByteArrayInputStream istream = new ByteArrayInputStream(getInstanceByteArray());

        try {
            OddjetConfiguration.ConfigurationProperties config = OddjetConfiguration.getConfiguration();

            OpenOfficeConnection connection = new SocketOpenOfficeConnection(config.openOfficeHost(), config.openOfficePort());
            connection.connect();

            DefaultDocumentFormatRegistry registry = new DefaultDocumentFormatRegistry();
            DocumentFormat outputFormat = registry.getFormatByFileExtension("pdf");
            DocumentFormat inputFormat = registry.getFormatByFileExtension("odt");
            OpenOfficeDocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(istream, inputFormat, ostream, outputFormat);

            connection.disconnect();

            return ostream.toByteArray();
        } catch (ConnectException e) {
            throw new OpenOfficeConnectionException(e);
        }
    }

    /**
     * Obtains a pdf print of an instance through {@link #getInstancePDFByteArray()} and attempts to save it as a file to the
     * given path.
     * 
     * @param path the path to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     * @throws OpenOfficeConnectionException if it fails to connect to the expected headless OpenOffice process.
     */
    public void saveInstancePDF(String path) throws DocumentLoadException, DocumentSaveException, OpenOfficeConnectionException {
        try {
            FileOutputStream ostream = new FileOutputStream(path);
            saveInstancePDF(ostream);
        } catch (FileNotFoundException e) {
            throw new DocumentSaveException(e);
        }
    }

    /**
     * Obtains a pdf print of an instance through {@link #getInstancePDFByteArray()} and attempts to save it to the given file.
     * 
     * @param file the file to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     * @throws OpenOfficeConnectionException if it fails to connect to the expected headless OpenOffice process.
     */
    public void saveInstancePDF(File file) throws DocumentLoadException, DocumentSaveException, OpenOfficeConnectionException {
        FileOutputStream ostream;
        try {
            ostream = new FileOutputStream(file);
            saveInstancePDF(ostream);
        } catch (FileNotFoundException e) {
            throw new DocumentSaveException(e);
        }
    }

    /**
     * Obtains a pdf print of an instance through {@link #getInstancePDFByteArray()} and attempts to save it to the given
     * OutputStream.
     * 
     * @param stream the OutputStream to save the instance document to.
     * @throws DocumentSaveException if the document can not be written to a byte array.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     * @throws OpenOfficeConnectionException if it fails to connect to the expected headless OpenOffice process.
     */
    public void saveInstancePDF(OutputStream stream) throws DocumentLoadException, DocumentSaveException,
            OpenOfficeConnectionException {
        try {
            stream.write(getInstancePDFByteArray());
        } catch (IOException e) {
            throw new DocumentSaveException(e);
        }
    }

    /**
     * Returns the page count of an instance. To do this an instance must be fully generated, which is time-consuming.
     * 
     * @return the page count of the template instance.
     * @throws DocumentLoadException if the document can not be created from the stored bytes.
     */
    public int getInstancePageCount() throws DocumentLoadException {
        TextDocument document = getInstance();
        try {
            OdfMetaDom meta = document.getMetaDom();
            Node statistics = meta.getElementsByTagName("meta:document-statistic").item(0);
            return Integer.parseInt(statistics.getAttributes().getNamedItem("meta:page-count").getNodeValue());
        } catch (Exception e) {
            throw new DocumentLoadException(e);
        }
    }

    private static void fillUserFields(TextDocument document, Map<String, Object> parameters, Locale locale) {
        NodeList nodes;
        try {
            nodes = document.getContentRoot().getElementsByTagName("text:user-field-decl");
        } catch (Exception e) {
            logger.error("Failed to create the file DOM while filling the user fields.");
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            String userFieldName = nodes.item(i).getAttributes().getNamedItem("text:name").getNodeValue();
            VariableField var = document.getVariableFieldByName(userFieldName);
            Object fieldValue = resolveAttributeChain(parameters, userFieldName);
            if (!(fieldValue instanceof ResolveFail)) {
                var.updateField(translate(fieldValue, locale), null);
            }
        }
    }

    /**
     * Resolves a chain of attributes by getting the first attribute's value from the root object, and from it getting the next
     * attribute's value successively until there are no attributes in the chain returning the last object evaluated. The
     * attribute may be a key in a map, a public method or field of the object, or an inaccessible field with an accessible
     * "get","is" or "has" method.
     * 
     * @param root the root object for the chain
     * @param attributeChain the chain of attributes to resolve. The attribute names in the chain are expected to be separated by
     *            dots.
     * @return the object that can be accessed following the provided attribute chain starting at the root object.
     */
    public static Object resolveAttributeChain(Object root, String attributeChain) {
        return resolveAttributeChain(root, getAttributeChainComponents(attributeChain));
    }

    // To distinguish fails from resolve successes where the result is null.
    private static class ResolveFail {
    };

    // Adapted from https://github.com/mbosecke/pebble/blob/master/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java#L43
    public static Object resolveAttributeChain(Object root, List<String> chainComponents) {
        Object result = root;
        for (String attributeName : chainComponents) {
            if (result == null) {
                logger.error("No matching attribute was found for '" + attributeName + "' since the parent object is null.");
                return new ResolveFail();
            }

            boolean found = false;
            if (!found) {
                if (result instanceof Map && ((Map<?, ?>) result).containsKey(attributeName)) {
                    result = ((Map<?, ?>) result).get(attributeName);
                    found = true;
                }
            }

            if (!found) {
                try {
                    Member member = null;
                    member = findMember(result, attributeName);
                    if (member != null) {
                        if (member instanceof Method) {
                            result = ((Method) member).invoke(result);
                            found = true;
                        } else if (member instanceof Field) {
                            result = ((Field) member).get(result);
                            found = true;
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error("Could not access attribute '" + attributeName + "'.");
                    return new ResolveFail();
                }
            }

            if (!found) {
                logger.warn("No matching attribute was found for '" + attributeName + "'. Assumed to be a static field.");
                return new ResolveFail();
            }
        }
        return result;
    }

    private static List<String> getAttributeChainComponents(String attributeChain) {
        return new ArrayList<String>(Arrays.asList(attributeChain.split(ATTRIBUTE_ACCESS_REGEX)));
    }

    // Copied from https://github.com/mbosecke/pebble/blob/master/src/main/java/com/mitchellbosecke/pebble/node/expression/GetAttributeExpression.java#L43
    private static Member findMember(Object object, String attributeName) throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        boolean found = false;
        Member result = null;

        // capitalize first letter of attribute for the following attempts
        String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

        // check get method
        if (!found) {
            try {
                result = clazz.getMethod("get" + attributeCapitalized);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check is method
        if (!found) {
            try {
                result = clazz.getMethod("is" + attributeCapitalized);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check has method
        if (!found) {
            try {
                result = clazz.getMethod("has" + attributeCapitalized);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // check if attribute is a public method
        if (!found) {
            try {
                result = clazz.getMethod(attributeName);
                found = true;
            } catch (NoSuchMethodException | SecurityException e) {
            }
        }

        // public field
        if (!found) {
            try {
                result = clazz.getField(attributeName);
                found = true;
            } catch (NoSuchFieldException | SecurityException e) {
            }
        }

        if (result != null) {
            ((AccessibleObject) result).setAccessible(true);
        }
        return result;
    }

    private static void fillTables(TextDocument document, Map<String, TableData> tableDataSources, Locale locale) {
        for (Table table : document.getTableList()) {

            TableCall ts = null;
            TableData td = null;
            try {
                ts = new TableCall(table.getTableName());
                td = tableDataSources.get(ts.getTableDataSourceName());
                if (td == null) {
                    logger.warn("No matching data was found for table " + ts.getTableName() + ", assumed to be static table.");
                }
            } catch (IllegalTableCallRepresentationException e) {
                logger.warn("Table name " + ts.getTableName()
                        + " does not conform to table call notation, assumed to be static table.");
            }
            if (ts != null && td != null) {
                TableConfiguration tp = ts.getParameters();
                TableCoordinate headers = tp.getHeader();
                TableCoordinate styleRCoord = tp.getStyleRelativeCoord();
                ContentStructure structure = tp.getContentStructure();
                int hCol = headers.getColumn();
                int hRow = headers.getRow();

                // Check if table has necessary cells predefined
                if (structure != ContentStructure.CATEGORICAL && (hRow >= table.getRowCount() || hCol >= table.getColumnCount())) {
                    logger.error("Table dimensions of " + table.getTableName()
                            + " do not allow the specification of the semantic data. Default category order assumed.");
                    structure = ContentStructure.POSITIONAL;
                }
                if ((styleRCoord != null && (hRow + styleRCoord.getRow() > table.getRowCount() || hCol + styleRCoord.getColumn() > table
                        .getColumnCount()))
                        || (tp.getLastBorderSourceSection() == LastBorderSourceSection.BODY && (table.getRowCount() == hRow || table
                                .getColumnCount() == hCol))) {
                    logger.error("Table dimensions of " + table.getTableName()
                            + " are not suficient to specify the table cell format. Default cell style will be used.");
                    styleRCoord = null;
                }

                // Collect all the styles of the predefined style cells before adding any new cells.
                //      This is only necessary due to a quirk in the simpleAPI where creating a new column/row changes the style of the cell
                //      in the previous column/row.
                Map<String, String> cellStyles = collectCellStyles(table, hCol, hRow, styleRCoord);
                Border lastBorder =
                        collectLastBorder(table, hCol, hRow, tp.getLastBorderSourceSection(), tp.getLastBorderSourceType());

                // Get the positional version of the data ( using the category order in the template table in the semantic case )
                List<List<Object>> data;
                if (structure == ContentStructure.CATEGORICAL) {
                    List<String> categoryOrder = null;
                    categoryOrder = getCategoryOrder(table, headers, tp.getContentDirection());
                    data = td.getData(categoryOrder);
                } else {
                    data = td.getData();
                }
                int X, Y, i, j, startX, startY, limitX, limitY, tableDimX, tableSpaceX, tableDimY, tableSpaceY, nData = 0;
                if (tp.getContentDirection() == ContentDirection.VERTICAL) {
                    startX = hCol;
                    startY = hRow;
                    tableDimX = table.getRowByIndex(hRow).getCellCount();
                    tableDimY = table.getColumnByIndex(hCol).getCellCount();
                } else {
                    startX = hRow;
                    startY = hCol;
                    tableDimX = table.getColumnByIndex(hCol).getCellCount();
                    tableDimY = table.getRowByIndex(hRow).getCellCount();
                }
                tableSpaceX = startY > 0 ? tableDimX - startX : -1;
                limitX = data.size();
                if (tableSpaceX > 0) {
                    if (tableSpaceX < limitX) {
                        limitX = tableSpaceX;
                        logger.warn("Too many data categories for the allocated table space in table '" + table.getTableName()
                                + "'. The remaining categories beyond table limits will be ignored.");
                    } else if (tableSpaceX > limitX) {
                        logger.warn("Too few data categories for the allocated table space in table '" + table.getTableName()
                                + "'. The remaining space will be empty.");
                    }
                }

                for (X = startX, i = 0; i < limitX; i++, X++) {
                    List<Object> dataCategory = data.get(i);

                    tableSpaceY = startX > 0 ? tableDimY - startY : -1;
                    boolean overflowReported = false;
                    limitY = dataCategory != null ? dataCategory.size() : 0;
                    if (tableSpaceY > 0) {
                        if (tableSpaceY < limitY) {
                            limitY = tableSpaceY;
                            logger.warn("Data category nr." + X
                                    + " has more data than the allocated table space allows for in table '"
                                    + table.getTableName() + "'. Remaining data will be ignored.");
                            overflowReported = true;
                        }
                    }
                    for (Y = startY, j = 0; j < limitY; j++, Y++) {
                        Cell cell =
                                tp.getContentDirection() == ContentDirection.VERTICAL ? table.getCellByPosition(X, Y) : table
                                        .getCellByPosition(Y, X);
                        switch (tp.getFillBehavior()) { //FIXME Fall through here allows cleaner code but it's a little less efficient.
                        case STEP:
                            // If there is a paragraph then don't do anything, else fall through
                            if (cell.getParagraphByIndex(0, true) != null) {
                                break;
                            }
                        case SKIP:
                            // If there is a paragraph then just rollback the data to be reused and recheck for data overflows, else fall through
                            if (cell.getParagraphByIndex(0, true) != null) {
                                j--;
                                nData--;
                                if (tableSpaceY > 0) {
                                    if (tableDimY - Y < limitY - j) {
                                        limitY = tableDimY - Y;
                                        if (!overflowReported) {
                                            logger.warn("Data category nr." + X
                                                    + " has more data than the allocated table space allows for in table '"
                                                    + table.getTableName() + "'. Remaining data will be ignored.");
                                            overflowReported = true;
                                        }
                                    }
                                }
                                break;
                            }
                        case APPEND:
                            // Get the last paragraph and if it exists add the data's text to it, else fall through
                            Paragraph lastParagraph = cell.getParagraphByReverseIndex(0, false);
                            if (lastParagraph != null) {
                                lastParagraph.getOdfElement().setTextContent(
                                        lastParagraph.getTextContent() + translate(dataCategory.get(j), locale));
                                break;
                            }
                        case PREPEND:
                            // Get the first paragraph and if it exists add the data's text to it, else fall through
                            Paragraph firstParagraph = cell.getParagraphByIndex(0, false);
                            if (firstParagraph != null) {
                                firstParagraph.getOdfElement().setTextContent(
                                        translate(dataCategory.get(j) + firstParagraph.getTextContent(), locale));
                                break;
                            }
                        case ADD:
                            // Add a new paragraph with the data's text
                            cell.addParagraph(translate(dataCategory.get(j), locale));
                            break;
                        default:
                            logger.error("Atempted to use unimplemented Fill Behavior: " + tp.getFillBehavior().name() + ".");
                        }
                        nData++;
                    }
                }
                // Create table relative automatic fields with table statistics
                String fieldName = ts.getTableName();
                Fields.createUserVariableField(document, fieldName + "_nRow", "" + table.getRowCount());
                Fields.createUserVariableField(document, fieldName + "_nCol", "" + table.getColumnCount());
                Fields.createUserVariableField(document, fieldName + "_nData", "" + nData);

                // Apply the correct formatting to each cell in the table
                if (cellStyles != null) {
                    int sCol = styleRCoord.getColumn();
                    int sRow = styleRCoord.getRow();
                    for (i = hCol; i < table.getColumnCount(); i++) {
                        for (j = hRow; j < table.getRowCount(); j++) {
                            Cell cell = table.getCellByPosition(i, j);
                            TableCoordinate styleCellCoord;
                            if (sCol == 0) {        // vertical
                                styleCellCoord = new TableCoordinate(i, j % sRow + hRow);
                            } else if (sRow == 0) { // horizontal
                                styleCellCoord = new TableCoordinate(i % sCol + hCol, j);
                            } else {                // periodic
                                int jumps = Math.min((i - hCol) / sCol, (j - hRow) / sRow);
                                styleCellCoord = new TableCoordinate(i - jumps * sCol, j - jumps * sRow);
                            }
                            // Copy style cell style properties
                            cell.setCellStyleName(cellStyles.get(styleCellCoord.toString()));
                            // Copy paragraph style
                            Cell styleCell = table.getCellByPosition(styleCellCoord.getColumn(), styleCellCoord.getRow());
                            Iterator<Paragraph> pit = cell.getParagraphIterator();
                            Iterator<Paragraph> spit = styleCell.getParagraphIterator();
                            while (pit.hasNext() && spit.hasNext()) {
                                //pit.next().setStyleName(spit.next().getStyleName()); //FIXME Not working, figure out why...
                                copyStyle(spit.next().getOdfElement(), pit.next().getOdfElement());
                            }
                        }
                    }
                }

                //Change the last border of the table
                if (lastBorder != null) {
                    CellBordersType lastBorderType;
                    CellRange lastCells = null;
                    if (tp.getContentDirection() == ContentDirection.VERTICAL) {
                        lastBorderType = CellBordersType.BOTTOM;
                        lastCells =
                                table.getCellRangeByPosition(headers.getColumn(), table.getRowCount() - 1,
                                        table.getColumnCount() - 1, table.getRowCount() - 1);
                    } else {
                        lastBorderType = CellBordersType.LEFT;
                        lastCells =
                                table.getCellRangeByPosition(table.getColumnCount() - 1, headers.getRow(),
                                        table.getColumnCount() - 1, table.getRowCount() - 1);
                    }
                    for (i = 0; i < lastCells.getColumnNumber(); i++) {
                        for (j = 0; j < lastCells.getRowNumber(); j++) {
                            lastCells.getCellByPosition(i, j).setBorders(lastBorderType, lastBorder);
                        }
                    }
                }
            }
        }

    }

    // XXX This breaks if the cells contain any "none" border attribute.
    private static Border collectLastBorder(Table table, int hCol, int hRow, LastBorderSourceSection lastBorderOrigin,
            CellBordersType lastBorderOriginType) {
        Border border = null;
        if (lastBorderOrigin != null) {
            border = Border.NONE;
            switch (lastBorderOrigin) {
            case HEADER:
                switch (lastBorderOriginType) {
                case LEFT:
                case TOP:
                    border = table.getCellByPosition(0, 0).getBorder(lastBorderOriginType);
                    break;
                case RIGHT:
                case BOTTOM:
                    border =
                            table.getCellByPosition((hCol != 0 ? hCol : table.getColumnCount()) - 1,
                                    (hRow != 0 ? hRow : table.getRowCount()) - 1).getBorder(lastBorderOriginType);
                    break;
                default:
                    break;
                }
                break;
            case BODY:
                switch (lastBorderOriginType) {
                case LEFT:
                case TOP:
                    border = table.getCellByPosition(hCol, hRow).getBorder(lastBorderOriginType);

                    break;
                case RIGHT:
                case BOTTOM:
                    border =
                            table.getCellByPosition(table.getColumnCount() - 1, table.getRowCount() - 1).getBorder(
                                    lastBorderOriginType);
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
        return border;
    }

    // XXX This may break if the element contains any "none" border attribute.
    private static boolean copyStyle(OdfStylableElement from, OdfStylableElement to) {
        if (to.getStyleFamily().equals(from.getStyleFamily())) {
            for (OdfStyleProperty prop : from.getStyleFamily().getProperties()) {
                String value = from.getProperty(prop);
                if (value != null) {
                    to.setProperty(prop, value);
                }
            }
            return true;
        };
        return false;
    }

    private static Map<String, String> collectCellStyles(Table table, int hCol, int hRow, TableCoordinate styleRCoord) {
        Map<String, String> cellStyles = null;
        if (styleRCoord != null) {
            cellStyles = new HashMap<>();
            for (int i = hCol; i < table.getColumnCount(); i++) {
                int limit = i > styleRCoord.getColumn() ? styleRCoord.getRow() + hRow : table.getRowCount();
                for (int j = hCol; j < limit; j++) {
                    cellStyles.put(new TableCoordinate(i, j).toString(), table.getCellByPosition(i, j).getStyleName());
                }
            }
        }
        return cellStyles;
    }

    private static List<String> getCategoryOrder(Table table, TableCoordinate headers, ContentDirection fdir) {
        List<String> categoryOrder = new ArrayList<String>();
        CellRange categoryRange = null;
        if (fdir == ContentDirection.VERTICAL) {
            categoryRange =
                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), table.getColumnCount() - 1,
                            headers.getRow());
        } else {
            categoryRange =
                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), headers.getColumn(),
                            table.getRowCount() - 1);
        }
        for (int i = 0; i < categoryRange.getColumnNumber(); i++) {
            for (int j = 0; j < categoryRange.getRowNumber(); j++) {
                Cell cell = categoryRange.getCellByPosition(i, j);
                Paragraph categoryParagraph = cell.getParagraphByIndex(0, false);
                String category = null;
                if (categoryParagraph == null || (category = categoryParagraph.getTextContent().trim()).isEmpty()) {
                    logger.warn("Data category missing at (" + i + "," + j + ") in table '" + table.getTableName() + "'.");
                    categoryOrder.add(null);
                } else {
                    cell.removeParagraph(categoryParagraph);
                    categoryOrder.add(category);
                };
            }
        }
        return categoryOrder;
    }

    private static String translate(Object o, Locale locale) {
        if (o == null) {
            return "";
        }
        try {
            Method m = o.getClass().getMethod("getContent", Locale.class);
            o = m.invoke(o, locale);
        } catch (Exception e) {
        }
        return o.toString();
    }
}
