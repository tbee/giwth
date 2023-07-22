package org.tbee.giwth.table;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TableProcessor<RowType> {

    private Function<Integer, RowType> onLineStart = (lineIdx) -> null;
    private BiConsumer<Integer, RowType> onLineEnd = (lineIdx, row) -> {};
    private OnFieldComplete<RowType> onFieldComplete = (rowIdx, colIdx, row, key, value) -> {};
    private BiConsumer<String, String> onField = (id, value) -> {};
    private final Map<String, BiConsumer<RowType, String>> onFieldString = new HashMap<>();

    /**
     * @param onLineStart called on start-of-data-line, zero-based line index
     * @return
     */
    public TableProcessor<RowType> onLineStart(Function<Integer, RowType> onLineStart) {
        this.onLineStart = onLineStart;
        return this;
    }

    /**
     * @param onLineEnd called on end-of-data-line, zero-based line index
     * @return
     */
    public TableProcessor<RowType> onLineEnd(BiConsumer<Integer, RowType> onLineEnd) {
        this.onLineEnd = onLineEnd;
        return this;
    }

    /**
     * @param onFieldComplete
     * @return
     */
    public TableProcessor<RowType> onField(OnFieldComplete<RowType> onFieldComplete) {
        this.onFieldComplete = onFieldComplete;
        return this;
    }

    /**
     * @param onField
     * @return
     */
    public TableProcessor<RowType> onField(BiConsumer<String, String> onField) {
        this.onField = onField;
        return this;
    }

    /**
     *
     * @param key
     * @param consumer
     * @return
     */
    public TableProcessor<RowType> onField(String key, BiConsumer<RowType, String> consumer) {
        onFieldString.put(key, consumer);
        return this;
    }

    /**
     *
     * @param contents
     */
    public void process(String contents) {

        // Split into lines
        String[] lines = contents.trim().split("\\r?\\n|\\r");

        // Use the first line to extract the headers
        List<String> headers = Arrays.stream(lines[0].split("\\|"))
                .skip(1)
                .map(h -> h.strip())
                .collect(Collectors.toList());

        // The rest of the lines are data
        for (int idx = 1; idx < lines.length; idx++) {
            int rowIdx = idx - 1;
            RowType row = onLineStart.apply(rowIdx);

            // Process the line
            List<String> values = Arrays.stream(lines[idx].split("\\|"))
                    .skip(1)
                    .map(h -> h.strip())
                    .collect(Collectors.toList());

            // Call out for each field in the line
            for (int colIdx = 0; colIdx < headers.size(); colIdx++) {
                String key = headers.get(colIdx);
                String value = values.get(colIdx);

                onFieldComplete.accept(rowIdx, colIdx, row, key, value);
                onField.accept(key, value);
                if (onFieldString.containsKey(key)) {
                    onFieldString.get(key).accept(row, value);
                }
            }

            onLineEnd.accept(rowIdx, row);
        }
    }
}