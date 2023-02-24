package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

public class StandardStepManual {

    static public StandardStepManual of() {
        return new StandardStepManual();
    }

    static public StandardStepManual ofStepParam(String v) {
        return of().stepParam(v);
    }

    public StandardStepManual stepParam(String v) {
        this.stepParam = v;
        return this;
    }
    private String stepParam = "default";

    public Given<StepContext> action(String actionArg) {
        return stepContext -> {
            stepContext.message = "stepParam=" + stepParam + ", actionArg=" + actionArg;
            System.out.println(stepContext.message);
            return stepContext;
        };
    }
}
