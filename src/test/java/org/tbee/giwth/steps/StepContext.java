package org.tbee.giwth.steps;

import java.util.ArrayList;
import java.util.List;

public class StepContext {
    public int numberOfStepsExecuted = 0;
    public int numberOfGivenStepsExecuted = 0;
    public int numberOfWhenStepsExecuted = 0;
    public int numberOfThenStepsExecuted = 0;

    public String message;
    public final List<String> trace = new ArrayList<>();

    public final List<User> users = new ArrayList<>();
}
