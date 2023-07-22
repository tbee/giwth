package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.StepContext;
import org.tbee.giwth.steps.UserStep;

public class TableTests {

    @Test
    public void basicTable() {

        StepContext stepContext = new StepContext();
        Scenario.of("basicTable", stepContext)
                .given(UserStep.exist(
                       """
                       | firstName | lastName | age |
                       | Donald    | Duck     | 50  |
                       | Mickey    | Mouse    | 55  |
                       | Dagobert  | Duck     | 70  |
                       """));

        Assertions.assertEquals(3, stepContext.users.size());
        Assertions.assertEquals("Donald", stepContext.users.get(0).firstName());
        Assertions.assertEquals("Mouse", stepContext.users.get(1).lastName());
        Assertions.assertEquals(70, stepContext.users.get(2).age());

        // Assert the unused callbacks
        Assertions.assertEquals(1, stepContext.trace.stream().filter(t -> t.contains("(0,2) age=50 for org.tbee.giwth.steps.User@")).count());
        Assertions.assertTrue(stepContext.trace.contains("firstName->Mickey"));
    }
}
