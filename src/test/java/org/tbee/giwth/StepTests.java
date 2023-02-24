package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.GeneratedStep;
import org.tbee.giwth.steps.ComplexStepManual;
import org.tbee.giwth.steps.SimpleStepManual;
import org.tbee.giwth.steps.StandardStepManual;
import org.tbee.giwth.steps.StepContext;

public class StepTests {

    @Test
    public void simpleManual() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(SimpleStepManual.action("value1"));

        Assertions.assertEquals("actionArg=value1", stepContext.message);
    }

    @Test
    public void standardManual() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(StandardStepManual.ofStepParam("value1").action("value2"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2", stepContext.message);
    }

    @Test
    public void complexManual() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(ComplexStepManual.ofStepParam("value1").action("value2").actionParam("value3"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2, actionParam=value3", stepContext.message);
    }

    @Test
    public void simpleGenerated() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(GeneratedStep.actionDirectly("value1"));

        Assertions.assertEquals("actionArg=value1", stepContext.message);
    }

    @Test
    public void standardGenerated() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(GeneratedStep.ofStepParam("value1").actionWithoutParams("value2"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2", stepContext.message);
    }

    @Test
    public void complexGenerated() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(GeneratedStep.ofStepParam("value1").actionWithParams("value2").actionParam("value3"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2, actionParam=value3", stepContext.message);
    }
}
