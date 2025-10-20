package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

public class ComplexStepManual {

    static public ComplexStepManual of() {
        return new ComplexStepManual();
    }

    static public ComplexStepManual ofStepParam(String v) {
        return of().stepParam(v);
    }

    public ComplexStepManual stepParam(String v) {
        this.stepParam = v;
        return this;
    }
    private String stepParam = "default";

    public Direct action(String actionArg) {
        Direct direct = new Direct();
        direct.actionArg = actionArg;
        return direct;
    }

    public class Direct implements Given<StepContext> {

        private String actionArg = "default";


        public Direct actionParam(String v) {
            this.actionParam = v;
            return this;
        }
        private String actionParam = "default";

        @Override
        public void run(StepContext stepContext) {
            stepContext.message = "stepParam=" + stepParam + ", actionArg=" + actionArg + ", actionParam=" + actionParam;
            System.out.println(stepContext.message);
        }
    }
}
