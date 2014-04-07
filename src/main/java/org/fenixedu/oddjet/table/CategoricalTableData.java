package org.fenixedu.oddjet.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoricalTableData extends TableData {

    private Map<String, List<Object>> categoricalData;

    public CategoricalTableData(Map<String, List<Object>> data) {
        this.categoricalData = data;
    }

    @Override
    public List<List<Object>> buildPositionalData(List<String> order) {
        List<List<Object>> positionalData = new ArrayList<>();
        for (String key : order) {
            if (key != null) {
                positionalData.add(categoricalData.get(key));
            } else {
                positionalData.add(new ArrayList<Object>());
            }
        }
        return positionalData;
    }

    @Override
    public List<List<Object>> buildPositionalData() {
        List<List<Object>> positionalData = new ArrayList<List<Object>>();
        for (String key : categoricalData.keySet()) {
            positionalData.add(categoricalData.get(key));
        }
        return positionalData;
    }

}
