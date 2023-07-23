package org.tbee.giwth.table;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Use the TableProcessor like this:
 *
 * <pre>{@code
 * static public Given<StepContext> exist(String table) {
 *     return stepContext -> {
 *         List<User> users = new ArrayList<>();
 *
 *         new TableProcessor<User>()
 *             .onLineStart(i -> new User())
 *             .onLineEnd((i, user) -> users.add(user))
 *
 *             .onField("firstName", (user, v) -> user.firstName(v))
 *             .onField("lastName", (user, v) -> user.lastName(v))
 *             .onField("age", (user, v) -> user.age(Integer.parseInt(v)))
 *
 *             .onField((rowIdx, colIdx, row, key, value) -> System.out.println("(" + rowIdx + "," + colIdx + ") " + key + "=" + value + " for " + row))
 *
 *             .process(table);
 *
 *         return stepContext;
 *     };
 * }
 *
 * Scenario.of("basicTable", stepContext)
 *         .given(Users.exist(
 *                """
 *                | firstName | lastName | age |
 *                | Donald    | Duck     | 40  |
 *                | Mickey    | Mouse    | 45  |
 *                | Dagobert  | Duck     | 60  |
 *                """))
 * }</pre>
 * @param <RowType>
 */
public class TableProcessor<RowType> {

    private Function<Integer, RowType> onLineStart = (lineIdx) -> null;
    private BiConsumer<Integer, RowType> onLineEnd = (lineIdx, row) -> {};
    private OnField<RowType> onField = (rowIdx, colIdx, row, key, value) -> {};
    private final Map<String, BiConsumer<RowType, String>> onSpecificField = new HashMap<>();

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
     * @param onField
     * @return
     */
    public TableProcessor<RowType> onField(OnField<RowType> onField) {
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
        onSpecificField.put(key, consumer);
        return this;
    }

    /**
     *
     * @param contents
     */
    public void process(String contents) {

        // Split into lines
        String[] lines = contents.trim().split("\\|(\\r?\\n|\\r)");

        // Use the first line to extract the headers
        List<String> headers = Arrays.stream(lines[0].split("\\|"))
                .skip(1)
                .map(h -> h.strip())
                .collect(Collectors.toList());

        // The rest of the lines are data
        for (int lineIdx = 1; lineIdx < lines.length; lineIdx++) {
            String line = lines[lineIdx];
            int rowIdx = lineIdx - 1;
            RowType row = onLineStart.apply(rowIdx);

            // Process the line
            String escapedPipePlaceholder = UUID.randomUUID().toString();
            List<String> values = Arrays.stream(line.replace("||", escapedPipePlaceholder).split("\\|"))
                    .skip(1)
                    .map(h -> h.replace(escapedPipePlaceholder, "|").strip())
                    .collect(Collectors.toList());

            // For each field in the line
            for (int colIdx = 0; colIdx < headers.size(); colIdx++) {
                String key = headers.get(colIdx);
                String value = values.get(colIdx);

                // Callbacks
                onField.accept(rowIdx, colIdx, row, key, value);
                if (onSpecificField.containsKey(key)) {
                    onSpecificField.get(key).accept(row, value);
                }
            }

            onLineEnd.accept(rowIdx, row);
        }
    }
}
