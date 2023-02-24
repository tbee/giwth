package org.tbee.giwth.steps;

/**
 * This code would be generated
 */
public class GeneratedStep extends GeneratedStepA {

    static public GeneratedStep of() {
        return new GeneratedStep();
    }

    static public GeneratedStep ofStepParam(String v) {
        return of().stepParam(v);
    }

    public GeneratedStep stepParam(String v) {
        this.stepParam = v;
        return this;
    }

    public Direct actionWithParams(String actionArg) {
        Direct direct = new Direct();
        direct.actionArg = actionArg;
        return direct;
    }

    public class Direct extends ActionWithParameters {

        public Direct actionParam(String v) {
            this.actionParam = v;
            return this;
        }
    }
}
