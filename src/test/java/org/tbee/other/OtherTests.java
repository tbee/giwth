package org.tbee.other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.Scenario;
import org.tbee.giwth.steps.CountingStep;
import org.tbee.giwth.steps.StepContext;

// This class is outside of the giwth package
public class OtherTests {

    @Test
    public void fullGWT() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(CountingStep.of().createGiven())
                .when(CountingStep.of().createWhen())
                .then(CountingStep.of().createThen());

        Assertions.assertEquals(3, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfThenStepsExecuted);
    }
}
