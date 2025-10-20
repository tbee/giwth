package org.tbee.giwth.builder;

import org.tbee.giwth.Given;
import org.tbee.giwth.builder.annotations.Arg;
import org.tbee.giwth.builder.annotations.Of;
import org.tbee.giwth.builder.annotations.Step;
import org.tbee.giwth.steps.StepContext;

/**
 * This is what the user would define
 */
@Step(stripSuffix = "A")
public class BuilderStepA {

    /**
     * Methods without any parameters can be written directly
     */
    static public Given<StepContext> actionDirectly(String actionArg) {
        return stepContext -> {
            stepContext.message = "actionArg=" + actionArg;
            System.out.println(stepContext.message);
        };
    }

    @Of
    String stepParam = "default";

    /**
     * Methods without action parameters can be written directly
     * @param actionArg
     * @return
     */
    public Given<StepContext> actionWithoutParams(String actionArg) {
        return stepContext -> {
            stepContext.message = "stepParam=" + stepParam + ", actionArg=" + actionArg;
            System.out.println(stepContext.message);
        };
    }

    /**
     * A class is needed to support actions with additional parameters.
     * The class name defines the method name.
     */
    abstract public class ActionWithParametersWithoutArgument implements Given<StepContext> {

        String actionParam = "default";

        @Override
        public void run(StepContext stepContext) {
            stepContext.message = "stepParam=" + stepParam + ", actionParam=" + actionParam;
            System.out.println(stepContext.message);
        }
    }

    /**
     * A class is needed to support actions with additional parameters.
     * The class name defines the method name.
     */
    public class ActionWithParameters implements Given<StepContext> {

        @Arg
        String actionArg1 = "default";
        @Arg
        double actionArg2 = 0;
        String actionParam = "default";

        @Override
        public void run(StepContext stepContext) {
            stepContext.message = "stepParam=" + stepParam + ", actionArg1=" + actionArg1 + ", actionArg2=" + actionArg2 + ", actionParam=" + actionParam;
            System.out.println(stepContext.message);
        }
    }
}
