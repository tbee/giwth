package org.tbee.giwth.steps;

import org.tbee.giwth.Given;
import org.tbee.giwth.table.TableProcessor;

import java.util.ArrayList;
import java.util.List;

public class UserStep {

    static public Given<StepContext> exist(String table) {
        return stepContext -> {
            List<User> users = new ArrayList<>();
            new TableProcessor<User>()
                    .onLineStart(i -> new User())
                    .onLineEnd((i, user) -> users.add(user))
                    .onField((key, value) -> System.out.println(key + "=" + value))
                    .onField("firstName", (user, v) -> user.firstName(v))
                    .onField("lastName", (user, v) -> user.lastName(v))
                    .onField("age", (user, v) -> user.age(Integer.parseInt(v)))
                    .process(table);
            return stepContext;
        };
    }
}
