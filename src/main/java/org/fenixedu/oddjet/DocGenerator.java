package org.fenixedu.oddjet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.oddjet.TableParameters.FillDirection;
import org.fenixedu.oddjet.TableParameters.FillType;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.VariableField;
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

    public static void generateDocument(Template template, String instancePath) throws Exception {
        TextDocument document = TextDocument.loadDocument(template.getOdtFilePath());
        fillUserFields(document, template.getParameters(), template.getLocale());
        fillTables(document, template.getTableDataSources(), template.getLocale());
        document.save(instancePath);
        document.close();
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
                if (styleRCoord != null
                        && (hRow + styleRCoord.getRow() > table.getRowCount() || hCol + styleRCoord.getColumn() > table
                                .getColumnCount())) {
                    System.err.println("Table dimensions of " + table.getTableName()
                            + " are not suficient to specify the table format.");
                    continue;
                }
                // Collect all the styles of the predefined style cells previously to adding any new cells.
                //      This is only necessary due to a quirk in the simpleAPI where creating a new column/row changes the style of the cell
                //      in the previous column/row.
                Map<String, Map<OdfStyleProperty, String>> cellStyles =
                        styleRCoord != null ? collectCellStyles(table, headers, styleRCoord) : null;

                // This is broken due to the borders in the simpleAPI not accounting for border="none"
                // List<Border> autoBorders = tp.shouldToggleLastBorder() ? collectAutoBorders(table, headers, tp.getFillDirection()) : null;

                // Get the positional version of the data ( using the category order in the template table in the semantic case )
                List<List<Object>> data;
                if (tp.getFillType() == FillType.CATEGORICAL) {
                    List<String> categoryOrder = null;
                    categoryOrder = getCategoryOrder(table, td.getData().keySet(), headers, tp.getFillDirection());
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
                    List<Object> dataObject = data.get(i);

                    tableSpaceY = startX > 0 ? tableDimY - startY : -1;
                    boolean overflowReported = false;
                    limitY = dataObject.size();
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
                        switch (tp.getFillBehavior()) { // Fall through here allows cleaner code but it's a little less efficient
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
                            Paragraph paragraph = cell.getParagraphByReverseIndex(0, false);
                            if (paragraph != null) {
                                paragraph.getOdfElement().setTextContent(
                                        paragraph.getTextContent() + translate(dataObject.get(j), locale));
                                break;
                            }
                        case ADD:
                            // Add a new paragraph with the data's text
                            cell.addParagraph(translate(dataObject.get(j), locale));
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
                            Map<OdfStyleProperty, String> properties = cellStyles.get(styleCellCoord.toString());
                            cell.getOdfElement().setProperties(properties);
                            // Remove any extra default properties
                            for (OdfStyleProperty p : cell.getOdfElement().getStyleFamily().getProperties()) {
                                if (!properties.containsKey(p)) {
                                    cell.getOdfElement().removeProperty(p);
                                }
                            }
                            // Copy paragraph style
                            Cell styleCell = table.getCellByPosition(styleCellCoord.getColumn(), styleCellCoord.getRow());
                            Iterator<Paragraph> pit = cell.getParagraphIterator();
                            Iterator<Paragraph> spit = styleCell.getParagraphIterator();
                            while (pit.hasNext() && spit.hasNext()) {
                                copyStyle(spit.next().getOdfElement(), pit.next().getOdfElement());
                            }
                        }
                    }
                }
                // This is broken due to the borders in the simpleAPI not accounting for border="none"
//                if (autoBorders != null) {
//                    CellBordersType lastBorderType;
//                    CellRange cellRange = null;
//                    if (tp.getFillDirection() == FillDirection.VERTICAL) {
//                        lastBorderType = CellBordersType.BOTTOM;
//                        cellRange =
//                                table.getCellRangeByPosition(headers.getColumn(), table.getRowCount() - 1,
//                                        table.getColumnCount() - 1, table.getRowCount() - 1);
//                    } else {
//                        lastBorderType = CellBordersType.LEFT;
//                        cellRange =
//                                table.getCellRangeByPosition(table.getColumnCount() - 1, headers.getRow(),
//                                        table.getColumnCount() - 1, table.getRowCount() - 1);
//                    }
//                    int b = 0;
//                    for (i = 0; i < cellRange.getColumnNumber(); i++) {
//                        for (j = 0; j < cellRange.getRowNumber(); j++) {
//                            Cell cell = cellRange.getCellByPosition(i, j);
//                            if (cell.getBorder(lastBorderType) != Border.NONE) {
//                                cell.setBorders(lastBorderType, autoBorders.get(b));
//                            } else {
//                                cell.setBorders(lastBorderType, Border.NONE);
//                            }
//                            b++;
//                        }
//                    }
//                }
            }
        }

    }

    //TODO Automatic Borders could be stripped along with the Categories to speed up the process. The cell ranges are the same.
    //     Consider joining  collectAutoBorders && getCategoryOrder methods
//    private static List<Border> collectAutoBorders(Table table, TableCoordenate headers, FillDirection fdir) {
//        List<Border> autoBorders = new ArrayList<>();
//        Border borderChoices[] = new Border[4];
//        CellBordersType firstBorderType;
//        CellRange cellRange = null;
//        if (fdir == FillDirection.VERTICAL) {
//            firstBorderType = CellBordersType.TOP;
//            cellRange =
//                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), table.getColumnCount() - 1,
//                            headers.getRow());
//            borderChoices[1] = cellRange.getCellByPosition(0, 0).getBorder(CellBordersType.LEFT);
//            borderChoices[2] = cellRange.getCellByPosition(cellRange.getColumnNumber() - 1, 0).getBorder(CellBordersType.RIGHT);
//        } else {
//            firstBorderType = CellBordersType.RIGHT;
//            cellRange =
//                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), headers.getColumn(),
//                            table.getRowCount() - 1);
//            borderChoices[1] = cellRange.getCellByPosition(0, 0).getBorder(CellBordersType.TOP);
//            borderChoices[2] = cellRange.getCellByPosition(0, cellRange.getRowNumber() - 1).getBorder(CellBordersType.BOTTOM);
//        }
//        borderChoices[3] = new Border(Color.BLACK, 1, SupportedLinearMeasure.PT);
//        for (int i = 0; i < cellRange.getColumnNumber(); i++) {
//            for (int j = 0; j < cellRange.getRowNumber(); j++) {
//                borderChoices[0] = cellRange.getCellByPosition(i, j).getBorder(firstBorderType);
//                for (int b = 0; b < 4; b++) {
//                    if (!borderChoices[b].equals(Border.NONE)) {
//                        autoBorders.add(borderChoices[b]);
//                        break;
//                    }
//                }
//            }
//        }
//        return autoBorders;
//    }

    private static boolean copyStyle(OdfStylableElement from, OdfStylableElement to) {
        if (to.getStyleFamily().equals(from.getStyleFamily())) {
            for (OdfStyleProperty prop : from.getStyleFamily().getProperties()) {
                String value = from.getProperty(prop);
                if (value != null && !value.equals("none")) {
                    to.setProperty(prop, value);
                }
            }
            return true;
        };
        return false;
    }

    private static Map<String, Map<OdfStyleProperty, String>> collectCellStyles(Table table, TableCoordenate headers,
            TableCoordenate styleRCoord) {
        Map<String, Map<OdfStyleProperty, String>> cellStyles = new HashMap<>();
        for (int i = headers.getColumn(); i < table.getColumnCount(); i++) {
            int limit = i > styleRCoord.getColumn() ? styleRCoord.getRow() + headers.getRow() : table.getRowCount();
            for (int j = headers.getColumn(); j < limit; j++) {
                TableTableCellElementBase el = table.getCellByPosition(i, j).getOdfElement();
                Map<OdfStyleProperty, String> propMap = new HashMap<>();
                for (OdfStyleProperty prop : el.getStyleFamily().getProperties()) {
                    String value = el.getProperty(prop);
                    if (value != null && !value.equals("none")) {
                        propMap.put(prop, value);
                    }
                }
                cellStyles.put(new TableCoordenate(i, j).toString(), propMap);
            }
        }
        return cellStyles;
    }

    private static List<String> getCategoryOrder(Table table, Collection<String> categories, TableCoordenate headers,
            FillDirection fdir) {
        List<String> categoryOrder = new ArrayList<String>();
        CellRange formulaRange = null;
        if (fdir == FillDirection.VERTICAL) {
            formulaRange =
                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), table.getColumnCount() - 1,
                            headers.getRow());
        } else {
            formulaRange =
                    table.getCellRangeByPosition(headers.getColumn(), headers.getRow(), headers.getColumn(),
                            table.getRowCount() - 1);
        }
        for (int i = 0; i < formulaRange.getColumnNumber(); i++) {
            for (int j = 0; j < formulaRange.getRowNumber(); j++) {
                Cell cell = formulaRange.getCellByPosition(i, j);
                Paragraph formula_paragraph = cell.getParagraphByIndex(0, false);
                String formula = null;
                if (formula_paragraph == null || (formula = formula_paragraph.getTextContent().trim()).isEmpty()) {
                    System.err.println("Data category missing at (" + i + "," + j + ") in table '" + table.getTableName() + "'.");
                } else if (!categories.contains(formula)) {
                    System.err.println("Unknown data category '" + formula + "' at (" + i + "," + j + ") in table '"
                            + table.getTableName() + "'.");
                } else {
                    cell.removeParagraph(formula_paragraph);
                }
                categoryOrder.add(formula);
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
