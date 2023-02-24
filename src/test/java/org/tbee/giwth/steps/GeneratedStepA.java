package org.tbee.giwth.steps;

import org.tbee.giwth.Given;

/**
 * This is what the user would define
 */
// @Step to trigger the builder generator
abstract public class GeneratedStepA {

    /**
     * Methods without any parameters can be written directly
     */
    static public Given<StepContext> actionDirectly(String actionArg) {
        return stepContext -> {
            stepContext.message = "actionArg=" + actionArg;
            System.out.println(stepContext.message);
            return stepContext;
        };
    }

    // @Of specifies it needs an ofStepParam() method as well, besides the stepParam()
    protected String stepParam = "default";

    /**
     * Methods without action parameters can be written directly
     * @param actionArg
     * @return
     */
    public Given<StepContext> actionWithoutParams(String actionArg) {
        return stepContext -> {
            stepContext.message = "stepParam=" + stepParam + ", actionArg=" + actionArg;
            System.out.println(stepContext.message);
            return stepContext;
        };
    }

    /**
     * A class is needed to support actions with additional parameters.
     * The class name defines the method name.
     */
    abstract public class ActionWithParameters implements Given<StepContext> {

        // @ActionArg specifies this will be part of the arguments of actionWithParameters()
        protected String actionArg = "default";
        protected String actionParam = "default";

        @Override
        public StepContext run(StepContext stepContext) {
            stepContext.message = "stepParam=" + stepParam + ", actionArg=" + actionArg + ", actionParam=" + actionParam;
            System.out.println(stepContext.message);
            return stepContext;
        }
    }
}
