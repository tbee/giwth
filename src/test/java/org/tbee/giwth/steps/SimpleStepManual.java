package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

public class SimpleStepManual {

    static public Given<StepContext> action(String actionArg) {
        return stepContext -> {
            stepContext.message = "actionArg=" + actionArg;
            System.out.println(stepContext.message);
            return stepContext;
        };
    }
}
