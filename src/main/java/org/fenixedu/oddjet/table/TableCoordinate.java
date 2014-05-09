package org.fenixedu.oddjet.table;

/**
 * Represents the coordinate of a cell in a table.
 * 
 * @author Gil Lacerda (gil.lacerda@tecnico.ulisboa.pt)
 * 
 */
public class TableCoordinate {

    /** column index, a non-negative integer */
    private int column;
    /** row index, a non-negative integer */
    private int row;

    /**
     * Constructs a new <code>TableCoordinate</code> with the provided row and column indexes.
     * 
     * @param column the cell's column index, a non-negative integer.
     * @param row the cell's row index, a non-negative integer.
     * @throws ArrayIndexOutOfBoundsException if either the provided column or row index is negative.
     */
    public TableCoordinate(int column, int row) {
        setColumn(column);
        setRow(row);
    }

    /**
     * Constructs a new <code>TableCoordinate</code> with row and column indexes set to 0.
     */
    public TableCoordinate() {
        this.column = 0;
        this.row = 0;
    };

    /**
     * Gets the column of the cell.
     * 
     * @return the column of the cell, a non-negative integer.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets the column of the coordinate.
     * 
     * @param column the cell's column index, a non-negative integer.
     * @throws ArrayIndexOutOfBoundsException if the provided column index is negative.
     */
    public void setColumn(int column) {
        if (column < 0) {
            throw new ArrayIndexOutOfBoundsException("Table column cannot be negative.");
        }
        this.column = column;
    }

    /**
     * Gets the row of the cell.
     * 
     * @return the row of the cell, a non-negative integer.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row of the coordinate.
     * 
     * @param row the cell's row index, a non-negative integer.
     * @throws ArrayIndexOutOfBoundsException if the provided row index is negative.
     */
    public void setRow(int row) {
        if (row < 0) {
            throw new ArrayIndexOutOfBoundsException("Table row cannot be negative.");
        }
        this.row = row;
    }

    /**
     * Compares this TableCoordinate to the specified object. The result is <code>true</code> if and only if the argument is not
     * null and is a TableCoordinate object that represents the same coordinate as this object, meaning their column and row
     * indexes are the same.
     * 
     * @param object the <code>TableCoordinate</code> to be compared with.
     * @return <code>true</code> if the given object represents a TableCoordinate equivalent to this TableCoordinate,
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof TableCoordinate && object != null) {
            TableCoordinate tableCoordinate = (TableCoordinate) object;
            return tableCoordinate.getColumn() == column && tableCoordinate.getRow() == row;
        } else {
            return false;
        }
    }

    /**
     * Returns a new <code>String</code> instance representing this <code>TableCoordinate</code>.
     * 
     * @return a string representation of this <code>TableCoordinate</code>
     */
    @Override
    public String toString() {
        return "(" + column + "," + row + ")";
    }
}