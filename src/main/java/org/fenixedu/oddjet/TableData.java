package org.fenixedu.oddjet;

import java.util.List;

public abstract class TableData {

    public abstract List<List<Object>> buildPositionalData();

    public abstract List<List<Object>> buildPositionalData(List<String> categoryOrder);
}
