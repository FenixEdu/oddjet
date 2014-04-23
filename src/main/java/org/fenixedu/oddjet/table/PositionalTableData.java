package org.fenixedu.oddjet.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the data to be used for filling a table in the template organized in a positional way. For ordering purposes indexes
 * can be used as categories.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class PositionalTableData implements TableData {

    List<List<Object>> data;

    /**
     * @param data an Iterable of Object Iterables where each Object Iterable corresponds to a column/row of the table and each
     *            Object to a cell's data.
     */
    public PositionalTableData(Iterable<Iterable<Object>> data) {
        this.data = new ArrayList<List<Object>>();
        for (Iterable<Object> it : data) {
            List<Object> list = new ArrayList<>();
            for (Object o : it) {
                list.add(o);
            }
            this.data.add(list);
        }
    }

    /**
     * @param data a two dimensional Object array where each Object corresponds to a cell's data.
     * 
     */
    public PositionalTableData(Object[][] data) {
        this.data = new ArrayList<List<Object>>();
        for (Object[] oa : data) {
            List<Object> list = new ArrayList<>();
            for (Object o : oa) {
                list.add(o);
            }
            this.data.add(list);
        }
    }

    @Override
    public List<List<Object>> getData(List<String> indexOrder) {
        List<List<Object>> data = new ArrayList<List<Object>>();
        int i;
        for (String index : indexOrder) {
            List<Object> dataList = null;
            if (index != null) {
                try {
                    i = Integer.parseInt(index);
                    if (i < this.data.size()) {
                        dataList = this.data.get(i);
                    } else {
                        System.err.println("Index '" + i + "' is out of bounds.");
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("Positional table data does not support non-integer categories such as '" + index + "'.");
                }
            }
            data.add(dataList);
        }
        return data;

    }

    @Override
    public List<List<Object>> getData() {
        return data;
    }

}
