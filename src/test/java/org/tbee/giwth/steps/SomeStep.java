package org.tbee.giwth.steps;

import org.tbee.giwth.Given;
import org.tbee.giwth.Then;
import org.tbee.giwth.When;

public class SomeStep {

    static public SomeStep of() {
        return new SomeStep();
    }

    public Given<StepContext> createGiven() {
        return stepContext -> {
            stepContext.numberOfStepsExecuted++;
            stepContext.numberOfGivenStepsExecuted++;
        };
    }

    public When<StepContext> createWhen() {
        return stepContext -> {
            stepContext.numberOfStepsExecuted++;
            stepContext.numberOfWhenStepsExecuted++;
        };
    }

    public Then<StepContext> createThen() {
        return stepContext -> {
            stepContext.numberOfStepsExecuted++;
            stepContext.numberOfThenStepsExecuted++;
        };
    }

    public Given<StepContext> failingGiven() {
        return stepContext -> {
            throw new IllegalStateException("fail");
        };
    }
}
