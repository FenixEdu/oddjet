package oddjet;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableSpecification {
    private TableData data;
    private TableParameters parameters;
    private String tableName;

    private final static Pattern TABLECALL = Pattern.compile("^(\\w+?)(_\\d+)?(?:\\((\\w+?(:?,\\w+?)*)\\))?$");

    private TableSpecification() {
    }

    public TableData getData() {
        return data;
    }

    public TableParameters getParameters() {
        return parameters;
    }

    public String getTableName() {
        return tableName;
    }

    public static TableSpecification build(String tableCall, Map<String, TableData> tableDataSources) {
        Matcher matcher = TABLECALL.matcher(tableCall);
        TableSpecification ts = null;
        if (matcher.find()) {
            String dataSourceName = matcher.group(1);
            TableData td = tableDataSources.get(dataSourceName);
            if (td != null) {
                ts = new TableSpecification();
                ts.data = td;
                ts.tableName = matcher.group(2) != null ? matcher.group(1) + matcher.group(2) : matcher.group(1);
                String tableParameters = matcher.group(3);
                ts.parameters = new TableParameters();
                if (tableParameters != null) {
                    String[] params = tableParameters.split(",");
                    for (String param : params) {
                        if (!TableParameters.Parameter.readInto(param, ts.parameters)) {
                            System.err.println("Unknown parameter " + param + " in table " + tableCall + ".");
                        }
                    }
                }
            } else {
                System.err.println("No matching data was found for table " + tableCall + ", assumed to be static table.");
            }
        } else {
            System.err.println("A table in the template document has an illegal name, " + tableCall + ".");
        }
        return ts;

    }
}
