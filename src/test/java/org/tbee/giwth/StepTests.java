package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.ComplexStep;
import org.tbee.giwth.steps.SimpleStep;
import org.tbee.giwth.steps.StepContext;

public class StepTests {

    @Test
    public void simple() {
        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(SimpleStep.action("value1"));

        Assertions.assertEquals("actionArg=value1", stepContext.message);
    }

    @Test
    public void complex() {
        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(ComplexStep.someGivenStatement().arg1("value1").arg2("value2").arg3("value3"));

        Assertions.assertEquals("arg1=value1, arg2=value2, arg3=value3", stepContext.message);
    }
}
