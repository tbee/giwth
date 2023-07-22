package org.tbee.giwth.table;

@FunctionalInterface
public interface OnField<RowType> {
    void accept(int rowIdx, int colIdx, RowType row, String key, String value);
}
