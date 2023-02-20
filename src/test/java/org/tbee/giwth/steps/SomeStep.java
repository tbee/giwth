package org.tbee.giwth.steps;

import org.tbee.giwth.Given;
import org.tbee.giwth.TestContext;
import org.tbee.giwth.Then;
import org.tbee.giwth.When;

public class SomeStep {

    static public SomeStep of() {
        return new SomeStep();
    }

    public Given<TestContext> createGiven() {
        return testContext -> {
            testContext.numberOfStepsExecuted++;
            testContext.numberOfGivenStepsExecuted++;
            return testContext;
        };
    }

    public When<TestContext> createWhen() {
        return testContext -> {
            testContext.numberOfStepsExecuted++;
            testContext.numberOfWhenStepsExecuted++;
            return testContext;
        };
    }

    public Then<TestContext> createThen() {
        return testContext -> {
            testContext.numberOfStepsExecuted++;
            testContext.numberOfThenStepsExecuted++;
            return testContext;
        };
    }

}
