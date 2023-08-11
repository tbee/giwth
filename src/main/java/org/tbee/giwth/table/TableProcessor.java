package org.tbee.giwth.table;

import java.util.*;
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
     * Process a table string and execute the configured callbacks
     * @param contents
     */
    public void process(String contents) {

        String[] lines = splitIntoLines(contents);
        List<String> headers = splitIntoFields(lines[0]);

        for (int lineIdx = 1; lineIdx < lines.length; lineIdx++) {
            String line = lines[lineIdx];
            int rowIdx = lineIdx - 1;
            List<String> values = splitIntoFields(line);

            RowType row = onLineStart.apply(rowIdx);
            for (int colIdx = 0; colIdx < headers.size(); colIdx++) {
                String key = headers.get(colIdx);
                String value = values.get(colIdx);

                onField.accept(rowIdx, colIdx, row, key, value);
                if (onSpecificField.containsKey(key)) {
                    onSpecificField.get(key).accept(row, value);
                }
            }
            onLineEnd.accept(rowIdx, row);
        }
    }

    private String[] splitIntoLines(String contents) {
        String[] lines = contents.trim().split("\\|(\\r?\\n|\\r)");
        return lines;
    }

    private List<String> splitIntoFields(String line) {
        String escapedPipePlaceholder = UUID.randomUUID().toString();
        List<String> values = Arrays.stream(line.replace("||", escapedPipePlaceholder).split("\\|"))
                .skip(1)
                .map(h -> h.replace(escapedPipePlaceholder, "|").strip())
                .collect(Collectors.toList());
        return values;
    }
}
