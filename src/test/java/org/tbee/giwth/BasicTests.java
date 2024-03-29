package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.SomeStep;
import org.tbee.giwth.steps.StepContext;

public class BasicTests {

    @Test
    public void scenario() {

        StepContext stepContext = new StepContext();
        Scenario<StepContext> scenario = Scenario.of("scenario description", stepContext);

        Assertions.assertEquals("scenario description", scenario.description());
        Assertions.assertEquals(stepContext, scenario.context());
    }

    @Test
    public void fullGWT() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(3, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void noGiven() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(2, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(0, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void splittedGiven() {

        StepContext stepContext = new StepContext();
        GivenAPI<StepContext> scenario = Scenario.of("keepItSimpleTest", stepContext)
                .given(SomeStep.of().createGiven());
        scenario.given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(4, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void cyclic() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen())
                .given(SomeStep.of().createGiven())
                .then(SomeStep.of().createThen())
                .when(SomeStep.of().createWhen());

        Assertions.assertEquals(6, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void andBut() {

        StepContext stepContext = new StepContext();
        Scenario.of("keepItSimpleTest", stepContext)
                .given(SomeStep.of().createGiven())
                .and(SomeStep.of().createGiven())
                .but(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .and(SomeStep.of().createWhen())
                .but(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen())
                .and(SomeStep.of().createThen())
                .but(SomeStep.of().createThen());

        Assertions.assertEquals(9, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(3, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(3, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(3, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void wrongOf() {
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            Scenario.of("keepItSimpleTest");
        });
        Assertions.assertEquals("Background(context) was not called prior to this method", thrown.getMessage());
    }

    @Test
    public void failingStep() {
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            StepContext stepContext = new StepContext();
            Scenario.of("keepItSimpleTest", stepContext)
                    .given(SomeStep.of().createGiven())
                    .when(SomeStep.of().createWhen())
                    .then(SomeStep.of().createThen())
                    .given(SomeStep.of().failingGiven());
        });
        Assertions.assertEquals("fail", thrown.getMessage());
    }
}
