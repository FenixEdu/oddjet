package org.fenixedu.oddjet.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fenixedu.oddjet.exception.IllegalTableCallRepresentationException;
import org.fenixedu.oddjet.exception.IllegalTableParameterRepresentationException;
import org.fenixedu.oddjet.exception.UnknownParameterTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the information present on a table call, including the table's configuration, identifier and data source name.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class TableCall {
    private TableConfiguration parameters;
    private String tableName;
    private String tableDataSourceName;
    private String tableId;

    /** Regex pattern to match table names. */
    private final static Pattern TABLESOURCENAME = Pattern.compile("\\w+?");
    /** Regex pattern to match table numbers. */
    private final static Pattern TABLEID = Pattern.compile("\\d+");
    /** Regex pattern to match table calls. */
    private final static Pattern TABLECALL = Pattern.compile("^(" + TABLESOURCENAME + ")(_" + TABLEID + ")?(?:\\(("
            + TableConfiguration.ParameterType.GENERIC.getPattern() + "(:?,"
            + TableConfiguration.ParameterType.GENERIC.getPattern() + ")*)\\))?$");

    private static final Logger logger = LoggerFactory.getLogger(TableCall.class);

    /**
     * @param tableCall the table call string representation. It is expected to conform to one of the following notations: [table
     *            name] or [table name]([table parameter],...), where [table name] = [data source name] or [data source name]_[id
     *            number] and [table parameter] is as defined in {@link TableConfiguration.ParameterType ParameterType}.
     * @throws IllegalTableCallRepresentationException if the provided string does not match a table call string representation.
     */
    public TableCall(String tableCall) throws IllegalTableCallRepresentationException {
        Matcher matcher = TABLECALL.matcher(tableCall);
        if (matcher.find()) {
            this.tableDataSourceName = matcher.group(1);
            this.tableName = matcher.group(1);
            this.tableId = "";
            if (matcher.group(2) != null) {
                this.tableName += matcher.group(2);
                this.tableId = matcher.group(2).substring(1);
            }
            this.parameters = new TableConfiguration();
            String tableParameters = matcher.group(3);
            if (tableParameters != null) {
                String[] params = tableParameters.split(",");
                for (String param : params) {
                    try {
                        TableConfiguration.ParameterType.readInto(param, this.parameters);
                    } catch (UnknownParameterTypeException e) {
                        logger.warn("Unknown Parameter " + param + " found while processing table call " + tableCall + ".");
                    } catch (IllegalTableParameterRepresentationException e) {
                        // technically this is not possible...
                        throw new IllegalTableCallRepresentationException(tableCall);
                    }
                }
            }
        } else {
            throw new IllegalTableCallRepresentationException(tableCall);
        }
    }

    /**
     * @return the table configuration parameters.
     */
    public TableConfiguration getParameters() {
        return parameters;
    }

    /**
     * @return the table name.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the table data source name.
     */
    public String getTableDataSourceName() {
        return tableDataSourceName;
    }

    /**
     * @return the table id.
     */
    public String getTableId() {
        return tableId;
    }

}
