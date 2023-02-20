package org.tbee.giwth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tbee.giwth.steps.SomeStep;

public class BasicTests {

    @Test
    public void fullGWT() {

        TestContext testContext = new TestContext();
        Scenario.of("keepItSimpleTest", testContext)
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(3, testContext.numberOfStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfThenStepsExecuted);
    }

    @Test
    public void noGiven() {

        TestContext testContext = new TestContext();
        Scenario.of("keepItSimpleTest", testContext)
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(2, testContext.numberOfStepsExecuted);
        Assertions.assertEquals(0, testContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfThenStepsExecuted);
    }

    @Test
    public void splittedGiven() {

        TestContext testContext = new TestContext();
        GivenAPI scenario = Scenario.of("keepItSimpleTest", testContext)
                .given(SomeStep.of().createGiven());
        scenario.given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen());

        Assertions.assertEquals(4, testContext.numberOfStepsExecuted);
        Assertions.assertEquals(2, testContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(1, testContext.numberOfThenStepsExecuted);
    }

    @Test
    public void cyclic() {

        TestContext testContext = new TestContext();
        Scenario.of("keepItSimpleTest", testContext)
                .given(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen())
                .given(SomeStep.of().createGiven())
                .then(SomeStep.of().createThen())
                .when(SomeStep.of().createWhen());

        Assertions.assertEquals(6, testContext.numberOfStepsExecuted);
        Assertions.assertEquals(2, testContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(2, testContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(2, testContext.numberOfThenStepsExecuted);
    }

    @Test
    public void andBut() {

        TestContext testContext = new TestContext();
        Scenario.of("keepItSimpleTest", testContext)
                .given(SomeStep.of().createGiven())
                .and(SomeStep.of().createGiven())
                .but(SomeStep.of().createGiven())
                .when(SomeStep.of().createWhen())
                .and(SomeStep.of().createWhen())
                .but(SomeStep.of().createWhen())
                .then(SomeStep.of().createThen())
                .and(SomeStep.of().createThen())
                .but(SomeStep.of().createThen());

        Assertions.assertEquals(9, testContext.numberOfStepsExecuted);
        Assertions.assertEquals(3, testContext.numberOfGivenStepsExecuted);
        Assertions.assertEquals(3, testContext.numberOfWhenStepsExecuted);
        Assertions.assertEquals(3, testContext.numberOfThenStepsExecuted);
    }
}
