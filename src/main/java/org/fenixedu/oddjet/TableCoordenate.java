package org.fenixedu.oddjet;

public class TableCoordenate {
    private int c;
    private int r;

    public int getColumn() {
        return c;
    }

    public void setColumn(int c) {
        if (c < 0) {
            throw new ArrayIndexOutOfBoundsException("Table coordenate cannot be negative.");
        }
        this.c = c;
    }

    public int getRow() {
        return r;
    }

    public void setRow(int r) {
        if (c < 0) {
            throw new ArrayIndexOutOfBoundsException("Table coordenate cannot be negative.");
        }
        this.r = r;
    }

    public TableCoordenate(int c, int r) {
        setColumn(c);
        setRow(r);
    }

    public TableCoordenate() {
    };

    public boolean equals(TableCoordenate tc) {
        return tc.getColumn() == c && tc.getRow() == r;
    }

    @Override
    public String toString() {
        return "(" + c + "," + r + ")";
    }
}