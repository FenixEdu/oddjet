package org.fenixedu.oddjet.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains the data to be used for filling a table in the template organized into straight-forward categories.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class CategoricalTableData implements TableData {

    private Map<String, List<Object>> categoricalData;

    /**
     * Constructs a CategoricalTableData from a mapping of category string representations to data object lists
     * 
     * @param data a mapping of category string representations to data object lists
     */
    public CategoricalTableData(Map<String, List<Object>> data) {
        this.categoricalData = data;
    }

    @Override
    public List<List<Object>> getData(List<String> order) {
        List<List<Object>> data = new ArrayList<>();
        for (String key : order) {
            if (key != null) {
                data.add(categoricalData.get(key));
            } else {
                data.add(null);
            }
        }
        return data;
    }

    @Override
    public List<List<Object>> getData() {
        List<List<Object>> data = new ArrayList<List<Object>>();
        for (String key : categoricalData.keySet()) {
            data.add(categoricalData.get(key));
        }
        return data;
    }

}
