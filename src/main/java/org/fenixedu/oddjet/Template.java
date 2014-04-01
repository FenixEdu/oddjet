package org.fenixedu.oddjet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

abstract public class Template {

    private Path filePath;
    private Locale locale;
    static final public String ATTRIBUTE_ACCESS_REGEX = "\\.";
    final private Map<String, Object> parameters = new HashMap<String, Object>();
    final private Map<String, TableData> tableDataSources = new HashMap<String, TableData>();

    public Template(String filePathString) throws UnexpectedTemplateFileTypeException, UnreadableTemplateFileException {
        if (new File(filePathString).canRead()) {
            if ((filePathString.endsWith(".odt") || filePathString.endsWith(".ott"))) {
                this.filePath = Paths.get(filePathString);
                this.locale = Locale.getDefault();
                populate();
            } else {
                throw new UnexpectedTemplateFileTypeException();
            }
        } else {
            throw new UnreadableTemplateFileException();

        }
    }

    public Template(String filePathString, Locale locale) throws UnexpectedTemplateFileTypeException {
        if (filePathString.endsWith(".odt") || filePathString.endsWith(".ott")) {
            this.filePath = Paths.get(filePathString);
            this.locale = locale;
            populate();
        } else {
            throw new UnexpectedTemplateFileTypeException();
        }
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

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
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
