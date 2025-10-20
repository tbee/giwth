package org.tbee.giwth.steps;

import org.tbee.giwth.Given;
import org.tbee.giwth.table.TableProcessor;

public class UserStep {

    static public Given<StepContext> exist(String table) {
        return stepContext -> {

            new TableProcessor<User>()
                    .onLineStart(i -> new User())
                    .onLineEnd((i, user) -> stepContext.users.add(user))
                    .onField((rowIdx, colIdx, row, key, value) -> stepContext.trace.add("(" + rowIdx + "," + colIdx + ") " + key + "=" + value + " for " + row))
                    .onField("firstName", (user, v) -> user.firstName(v))
                    .onField("lastName", (user, v) -> user.lastName(v))
                    .onField("age", (user, v) -> user.age(Integer.parseInt(v)))
                    .process(table);
        };
    }
}
