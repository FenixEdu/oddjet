package org.fenixedu.oddjet.table;

import java.util.List;

/**
 * Defines the necessary methods for classes that contain the data to be used for filling a table in the template.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public interface TableData {

    /**
     * Gets the data to be used in filling a template table with the default category ordering.
     * 
     * @return a list of lists of objects containing the data for the table. The internal lists correspond to data categories that
     *         will be expanded into a column or row of the table. Each data object is to be mapped to a cell within that range.
     *         The category/data object order is to be maintained and assumed continuous in the table generation. The lists do not
     *         necessarily need to have the same length as their table counterparts and for static cells/columns/rows null values
     *         can be used. The lists are ordered according to some default order coded in implementing classes.
     */
    public List<List<Object>> getData();

    /**
     * Gets the data to be used in filling a template table with the specified category ordering.
     * 
     * @param categoryOrder a list of category string representations encoding the order of the corresponding internal lists. This
     *            list may have null values to insert empty category lists.
     * @return a list of lists of objects containing the data for the table. The internal lists correspond to data categories that
     *         will be expanded into a column or row of the table. Each data object is to be mapped to a cell within that range.
     *         The category/data object order is to be maintained and assumed continuous in the table generation. The lists do not
     *         necessarily need to have the same length as their table counterparts and for static cells/columns/rows null values
     *         can be used. The category lists must be ordered according to the order of the categories in the provided list.
     */
    public List<List<Object>> getData(List<String> categoryOrder);
}
