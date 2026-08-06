package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

public class ComplexStep {

    static public SomeGivenStatement someGivenStatement() {
        return new SomeGivenStatement();
    }
    public static class SomeGivenStatement implements Given<StepContext> {

        public SomeGivenStatement arg1(String v) {
            this.arg1 = v;
            return this;
        }
        private String arg1 = "default";

        public SomeGivenStatement arg2(String actionArg) {
            this.arg2 = actionArg;
            return this;
        }
        private String arg2 = "default";

        public SomeGivenStatement arg3(String v) {
            this.arg3 = v;
            return this;
        }
        private String arg3 = "default";

        @Override
        public void run(StepContext stepContext) {
            stepContext.message = "arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3;
            System.out.println(stepContext.message);
        }
    }
}
