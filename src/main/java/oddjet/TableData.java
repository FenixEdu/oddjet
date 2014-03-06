package oddjet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableData {
    //TODO implement more constructors and support data ordering when building the positional data

    private Map<String, List<Object>> data = null;

    public TableData(Iterable<Iterable<Object>> data) {
        this.data = new LinkedHashMap<>();
        int idx = 0;
        Iterator<Iterable<Object>> cit = data.iterator();
        while (cit.hasNext()) {
            List<Object> list = new ArrayList<>();
            Iterator<Object> dit = cit.next().iterator();
            while (dit.hasNext()) {
                list.add(dit.next());
            }
            this.data.put("" + idx, list);
            idx++;
        }
    }

    public TableData(Map<String, List<Object>> data) {
        this.data = data;
    }

    public List<List<Object>> buildPositionalData(List<String> order) {
        List<List<Object>> positionalData = new ArrayList<>();
        for (String key : order) {
            if (key != null) {
                positionalData.add(data.get(key));
            } else {
                positionalData.add(new ArrayList<Object>());
            }
        }
        return positionalData;
    }

    public List<List<Object>> buildPositionalData() {
        List<List<Object>> positionalData = new ArrayList<List<Object>>();
        for (String key : data.keySet()) {
            positionalData.add(data.get(key));
        }
        return positionalData;
    }

    public Map<String, List<Object>> getData() {
        return data;
    }

}
