package org.fenixedu.oddjet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

abstract public class Template implements Serializable {

    private String odtFilePath;
    private Locale locale;
    static final public String ATTRIBUTE_ACCESS_REGEX = "\\.";
    final private Map<String, Object> parameters = new HashMap<String, Object>();
    final private Map<String, TableData> tableDataSources = new HashMap<String, TableData>();

    public Template(String odtFilePath) {
        this.odtFilePath = odtFilePath;
        this.locale = Locale.getDefault();
        populate();
    }

    public Template(String odtFilePath, Locale locale) {
        this.odtFilePath = odtFilePath;
        this.locale = locale;
        populate();
    }

    public final Map<String, Object> getParameters() {
        return parameters;
    }

    public void addParameter(final String key, final Object value) {
        this.parameters.put(key, value);
    }

    public void addTableDataSource(final String key, final TableData value) {
        this.tableDataSources.put(key, value);
    }

    public Map<String, TableData> getTableDataSources() {
        return tableDataSources;
    }

    public String getOdtFilePath() {
        return odtFilePath;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    abstract protected void populate();

    abstract public String getReportFileName();
}
