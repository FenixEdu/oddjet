package org.fenixedu.oddjet;

import java.util.ArrayList;
import java.util.List;

public class PositionalTableData extends TableData {

    List<List<Object>> data;

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
    public List<List<Object>> buildPositionalData(List<String> indexOrder) {
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
    public List<List<Object>> buildPositionalData() {
        return data;
    }

}
