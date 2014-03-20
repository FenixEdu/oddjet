package org.fenixedu.oddjet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ListTableData extends TableData {

    List<Object> data;

    public ListTableData(Iterable<Object> data) {
        this.data = new ArrayList<>();
        for (Object o : data) {
            this.data.add(o);
        }
    }

    @Override
    public List<List<Object>> buildPositionalData(List<String> attributeOrder) {
        List<List<Object>> data = new ArrayList<List<Object>>();
        List<String> getterOrder = new ArrayList<String>();
        for (String attribute : attributeOrder) {
            if (attribute != null) {
                getterOrder.add("get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1));
            } else {
                getterOrder.add(null);
            }
            data.add(new ArrayList<Object>());
        }
        for (int i = 0; i < this.data.size(); i++) {
            Object o = this.data.get(i);
            int idx = 0;
            for (String getterName : getterOrder) {
                Object attribute = null;
                if (getterName != null) {
                    try {
                        Method getter = o.getClass().getMethod(getterName);
                        attribute = getter.invoke(o);
                    } catch (NoSuchMethodException nsme) {
                        System.err.println("No matching getter was found for the attribute named '" + attribute
                                + "' in object nr." + i + ".");
                    } catch (SecurityException se) {
                        System.err.println("Non-public getter for the attribute named '" + attribute + "' in object nr." + i
                                + ".");
                    } catch (IllegalAccessException iae) {
                        System.err.println("The getter for attribute '" + attribute + "' could not be accessed in object nr." + i
                                + ".");
                    } catch (InvocationTargetException ite) {
                        System.err.println("Exception ocurred in the getter for attribute '" + attribute + "' in object nr." + i
                                + ":");
                        ite.printStackTrace();
                    }
                }
                data.get(idx).add(attribute);
                idx++;
            }
        }
        return data;
    }

    @Override
    public List<List<Object>> buildPositionalData() {
        List<List<Object>> positionalData = new ArrayList<List<Object>>();
        positionalData.add(data);
        return positionalData;
    }
}
