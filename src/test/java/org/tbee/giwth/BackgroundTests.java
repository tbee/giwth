package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.SomeStep;
import org.tbee.giwth.steps.StepContext;

import java.util.concurrent.atomic.AtomicReference;

public class BackgroundTests {

    @BeforeEach
    public void beforeEach() {
        Scenario.background(new StepContext())
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());
    }

    @Test
    public void basic() {

        // For getting hold of the context
        AtomicReference<StepContext> stepContextReference = new AtomicReference<StepContext>();

        Scenario.<StepContext>of("keepItSimpleTest")
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen())
                // For getting hold of the context
                .and(stepContext -> {
                    stepContextReference.set(stepContext);
                    return stepContext;
                });

        StepContext stepContext = stepContextReference.get();
        Assertions.assertEquals(6, stepContext.numberOfStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(2, stepContext.numberOfThenStepsExecuted);
    }

    @Test
    public void wrongOf() {
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            Scenario.of("keepItSimpleTest", new StepContext());
        });
        Assertions.assertEquals("Background(context) was already called, please us of(context) instead of this method", thrown.getMessage());
    }

    @Test
    public void twoBackgrounds() {
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            Scenario.background(new StepContext()); // first on is in the beforeEach
        });
        Assertions.assertEquals("Background(context) was already called", thrown.getMessage());
    }

    @Test
    public void twoOfs() {
        Scenario.of("keepItSimpleTest");
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            Scenario.of("keepItSimpleTest");
        });
        Assertions.assertEquals("Background(context) was not called prior to this method", thrown.getMessage());
    }
}
