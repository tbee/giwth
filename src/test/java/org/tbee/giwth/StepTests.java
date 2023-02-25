package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.builder.BuilderStep;
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
    public void simpleBuild() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(BuilderStep.actionDirectly("value1"));

        Assertions.assertEquals("actionArg=value1", stepContext.message);
    }

    @Test
    public void standardBuild() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(BuilderStep.of().stepParam("value1").actionWithoutParams("value2"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2", stepContext.message);
    }

    @Test
    public void standardBuildOf() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(BuilderStep.ofStepParam("value1").actionWithoutParams("value2"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2", stepContext.message);
    }

    @Test
    public void complexBuild() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(BuilderStep.ofStepParam("value1").actionWithParametersWithoutArgument().actionParam("value3"));
        Assertions.assertEquals("stepParam=value1, actionParam=value3", stepContext.message);
    }

    @Test
    public void complexBuildParam() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(BuilderStep.ofStepParam("value1").actionWithParameters("value2").actionParam("value3"));

        Assertions.assertEquals("stepParam=value1, actionArg=value2, actionParam=value3", stepContext.message);
    }
}
