package org.fenixedu.oddjet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fenixedu.oddjet.TableParameters.FillDirection;
import org.fenixedu.oddjet.TableParameters.FillType;
import org.fenixedu.oddjet.TableParameters.LastBorderOrigin;
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
import org.w3c.dom.NodeList;

public class DocGenerator {
    //TODO refactor code some more
    //TODO support category data slicing
    //TODO support lists for user fields for repeating enclosing paragraphs (for now use tables)
    //TODO support itemization frames (bullets & numbers)
    //TODO build a critical error/error/warning/message system to organize to improve the prints

    public static void generateDocument(Template template, String instancePath) {
        TextDocument document = null;
        try {
            document = TextDocument.loadDocument(template.getFilePath().toString());
        } catch (Exception e) {
            System.err.println("Unable to load the template " + template.getFilePath().toString());
            e.printStackTrace();
            return;
        }
        fillUserFields(document, template.getParameters(), template.getLocale());
        fillTables(document, template.getTableDataSources(), template.getLocale());
        try {
            document.save(instancePath);
        } catch (Exception e) {
            System.err.println("Unable to save the template instance document " + instancePath);
            e.printStackTrace();
            return;
        }
        document.close();
    }

    public static void generatePdf(String instancePath, String outDir) {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec("soffice --headless --convert-to pdf --outdir " + outDir + " " + instancePath);
            IOUtils.copy(pr.getErrorStream(), System.err);
            int code = pr.waitFor();
            if (code != 0) {
                System.err.println("Failed to convert instance " + instancePath + " to pdf.");
            }
        } catch (IOException e) {
            System.err.println("Failed to convert instance " + instancePath + " to pdf.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] generatePdfByteArray(Template template, String outDir, boolean cleanup) {
        String filename = template.getFilePath().getFileName().toString();
        String instancePath =
                Paths.get(outDir).resolve(new StringBuilder(filename).insert(filename.length() - 4, "Instance").toString())
                        .toString();
        generateDocument(template, instancePath);
        generatePdf(instancePath, outDir);
        String pdfPath = instancePath.substring(0, instancePath.length() - 4) + ".pdf";
        byte[] content = null;
        try {
            content = FileUtils.readFileToByteArray(new File(pdfPath));
        } catch (IOException e) {
            System.err.println("Failed to read pdf document " + pdfPath + ".");
            e.printStackTrace();
        }
        if (cleanup) {
            if (!(new File(instancePath).delete() && new File(pdfPath).delete())) {
                System.err.println("Unable to cleanup temporary files.");
            }
        }
        return content;
    }

    public static byte[] generatePdfByteArray(Template template, String tempDir) {
        return generatePdfByteArray(template, tempDir, false);
    }

    private static void fillUserFields(TextDocument document, Map<String, Object> parameters, Locale locale) {
        NodeList nodes;
        try {
            nodes = document.getContentRoot().getElementsByTagName("text:user-field-decl");
        } catch (Exception e) {
            System.err.println("Failed to create the file DOM while filling the user fields.");
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            String userFieldName = nodes.item(i).getAttributes().getNamedItem("text:name").getNodeValue();
            String[] nameComponents = userFieldName.split(Template.ATTRIBUTE_ACCESS_REGEX);
            Object obj = parameters.get(nameComponents[0]);
            VariableField var = document.getVariableFieldByName(userFieldName);
            if (obj != null) {
                for (int c = 1; c < nameComponents.length; c++) {
                    if (!nameComponents[c].isEmpty()) {
                        String getterName =
                                "get" + nameComponents[c].substring(0, 1).toUpperCase() + nameComponents[c].substring(1);
                        try {
                            Method getter = obj.getClass().getMethod(getterName);
                            obj = getter.invoke(obj);
                        } catch (NoSuchMethodException nsme) {
                            System.err.println("No matching getter was found for the attribute named '" + nameComponents[c]
                                    + "' while trying to evaluate '" + userFieldName + "'.");
                        } catch (SecurityException se) {
                            System.err.println("Non-public getter for the attribute named '" + nameComponents[c]
                                    + "' found while trying to evaluate '" + userFieldName + "'.");
                        } catch (IllegalAccessException iae) {
                            System.err.println("The getter for attribute '" + nameComponents[c] + "' could not be accessed.");

                        } catch (InvocationTargetException ite) {
                            System.err.println("Exception ocurred in the getter for attribute '" + nameComponents[c]
                                    + "' while trying to evaluate '" + userFieldName + "':");
                            ite.printStackTrace();
                        }
                    }
                }
                var.updateField(translate(obj, locale), null);
            } else {
                System.err.println("No matching parameter was found for the user field named '" + userFieldName
                        + "'. Assuming the field is static.");
            }
        }
    }

    private static void fillTables(TextDocument document, Map<String, TableData> tableDataSources, Locale locale) {
        for (Table table : document.getTableList()) {
            TableSpecification ts = TableSpecification.build(table.getTableName(), tableDataSources);
            if (ts != null) {
                TableData td = ts.getData();
                TableParameters tp = ts.getParameters();
                TableCoordenate headers = tp.getHeaders();
                TableCoordenate styleRCoord = tp.getStyleRCoord();
                int hCol = headers.getColumn();
                int hRow = headers.getRow();

                // Check if table has necessary cells predefined
                if (tp.getFillType() != FillType.CATEGORICAL && (hRow >= table.getRowCount() || hCol >= table.getColumnCount())) {
                    System.err.println("Table dimensions of " + table.getTableName()
                            + " do not allow the specification of the semantic data.");
                    continue;
                }
                if ((styleRCoord != null && (hRow + styleRCoord.getRow() > table.getRowCount() || hCol + styleRCoord.getColumn() > table
                        .getColumnCount()))
                        || (tp.getLastBorderOrigin() == LastBorderOrigin.BODY && (table.getRowCount() == hRow || table
                                .getColumnCount() == hCol))) {
                    System.err.println("Table dimensions of " + table.getTableName()
                            + " are not suficient to specify the table cell format.");
                    continue;
                }

                // Collect all the styles of the predefined style cells before adding any new cells.
                //      This is only necessary due to a quirk in the simpleAPI where creating a new column/row changes the style of the cell
                //      in the previous column/row.
                Map<String, String> cellStyles = collectCellStyles(table, hCol, hRow, styleRCoord);
                Border lastBorder = collectLastBorder(table, hCol, hRow, tp.getLastBorderOrigin(), tp.getLastBorderOriginType());

                // Get the positional version of the data ( using the category order in the template table in the semantic case )
                List<List<Object>> data;
                if (tp.getFillType() == FillType.CATEGORICAL) {
                    List<String> categoryOrder = null;
                    categoryOrder = getCategoryOrder(table, headers, tp.getFillDirection());
                    data = td.buildPositionalData(categoryOrder);
                } else {
                    data = td.buildPositionalData();
                }
                int X, Y, i, j, startX, startY, limitX, limitY, tableDimX, tableSpaceX, tableDimY, tableSpaceY, nData = 0;
                if (tp.getFillDirection() == FillDirection.VERTICAL) {
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
                        System.err.println("Too many data categories for the allocated table space in table '"
                                + table.getTableName() + "'. The remaining categories beyond table limits will be ignored.");
                    } else if (tableSpaceX > limitX) {
                        System.err.println("Too few data categories for the allocated table space in table '"
                                + table.getTableName() + "'. The remaining space will be empty.");
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
                            System.err.println("Data category nr." + X
                                    + " has more data than the allocated table space allows for in table '"
                                    + table.getTableName() + "'. Remaining data will be ignored.");
                            overflowReported = true;
                        }
                    }
                    for (Y = startY, j = 0; j < limitY; j++, Y++) {
                        Cell cell =
                                tp.getFillDirection() == FillDirection.VERTICAL ? table.getCellByPosition(X, Y) : table
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
                                            System.err.println("Data category nr." + X
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
                            System.err.println("Atempted to use unimplemented Fill Behavior: " + tp.getFillBehavior().name()
                                    + ".");
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
                            TableCoordenate styleCellCoord;
                            if (sCol == 0) {        // vertical
                                styleCellCoord = new TableCoordenate(i, j % sRow + hRow);
                            } else if (sRow == 0) { // horizontal
                                styleCellCoord = new TableCoordenate(i % sCol + hCol, j);
                            } else {                // periodic
                                int jumps = Math.min((i - hCol) / sCol, (j - hRow) / sRow);
                                styleCellCoord = new TableCoordenate(i - jumps * sCol, j - jumps * sRow);
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
                    if (tp.getFillDirection() == FillDirection.VERTICAL) {
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
    private static Border collectLastBorder(Table table, int hCol, int hRow, LastBorderOrigin lastBorderOrigin,
            CellBordersType lastBorderOriginType) {
        Border border = null;
        if (lastBorderOrigin != null) {
            if (hRow != 0 && hCol != 0) {
                System.err.println("Table " + table.getTableName()
                        + " borders can be fully specified within the template file. Last Border Parameter will be ignored.");
            } else {
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

    private static Map<String, String> collectCellStyles(Table table, int hCol, int hRow, TableCoordenate styleRCoord) {
        Map<String, String> cellStyles = null;
        if (styleRCoord != null) {
            cellStyles = new HashMap<>();
            for (int i = hCol; i < table.getColumnCount(); i++) {
                int limit = i > styleRCoord.getColumn() ? styleRCoord.getRow() + hRow : table.getRowCount();
                for (int j = hCol; j < limit; j++) {
                    cellStyles.put(new TableCoordenate(i, j).toString(), table.getCellByPosition(i, j).getStyleName());
                }
            }
        }
        return cellStyles;
    }

    private static List<String> getCategoryOrder(Table table, TableCoordenate headers, FillDirection fdir) {
        List<String> categoryOrder = new ArrayList<String>();
        CellRange categoryRange = null;
        if (fdir == FillDirection.VERTICAL) {
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
                    System.err.println("Data category missing at (" + i + "," + j + ") in table '" + table.getTableName() + "'.");
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
