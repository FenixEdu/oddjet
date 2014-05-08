package org.fenixedu.oddjet.table;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.oddjet.Template;
import org.fenixedu.oddjet.exception.AttributeChainResolutionFailureException;

/**
 * Contains the data to be used for filling a table in the template organized into a list of entries. Data categories are taken to
 * be attributes of the entry objects.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class EntryListTableData implements TableData {

    private List<Object> data;

    /**
     * @param data an Object Iterable containing the entry objects. Each entry object corresponds to a column/row in the table and
     *            its attributes can be used as categories.
     */
    public EntryListTableData(Iterable<Object> data) {
        this.data = new ArrayList<>();
        for (Object o : data) {
            this.data.add(o);
        }
    }

    @Override
    public List<List<Object>> getData(List<String> attributeOrder) {
        List<List<Object>> data = new ArrayList<List<Object>>();
        for (int i = 0; i < attributeOrder.size(); i++) {
            data.add(new ArrayList<Object>());
        }
        for (int i = 0; i < this.data.size(); i++) {
            Object o = this.data.get(i);
            int idx = 0;
            for (String attribute : attributeOrder) {
                try {
                    data.get(idx).add(Template.resolveAttributeChain(o, attribute));
                } catch (AttributeChainResolutionFailureException e) {
                    data.get(idx).add(null);
                }
                idx++;
            }
        }
        return data;
    }

    @Override
    public List<List<Object>> getData() {
        List<List<Object>> positionalData = new ArrayList<List<Object>>();
        positionalData.add(data);
        return positionalData;
    }
}
