package org.tbee.giwth;

import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.*;

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
        //Assertions.assertEquals("actionArg=value1", stepContext.message);
    }
}
