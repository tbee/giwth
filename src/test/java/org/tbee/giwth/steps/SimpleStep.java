package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

public class SimpleStep {

    static public Given<StepContext> action(String actionArg) {
        return stepContext -> {
            stepContext.message = "actionArg=" + actionArg;
            System.out.println(stepContext.message);
        };
    }
}
