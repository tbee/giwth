package org.tbee.giwth;

public class Scenario<Context> implements GivenAPI<Context>, WhenAPI<Context>, ThenAPI<Context> {

    static private ThreadLocal<Scenario<?>> background = new ThreadLocal<Scenario<?>>();

    private String description;
    private Context context;

    private Scenario(String description, Context context) {
        this.description = description;
        this.context = context;
    }

    /**
     * Start a new scenario
     * @param description
     * @param context
     * @return
     * @param <Context>
     */
    static public <Context> Scenario<Context> of(String description, Context context) {
        if (background.get() != null) {
            background.set(null); // required to make test run correctly
            throw new IllegalStateException("Background(context) was already called, please us of(context) instead of this method");
        }
        return new Scenario<Context>(description, context);
    }

    /**
     * Set a background for future scenario's, use in combination with of(description).
     * @param context
     * @return
     * @param <Context>
     */
    static public <Context> Scenario<Context> background(Context context) {
        if (background.get() != null) {
            background.set(null); // required to make test run correctly
            throw new IllegalStateException("Background(context) was already called");
        }
        Scenario<Context> scenario = new Scenario<>("<background>", context);
        background.set(scenario);
        return scenario;
    }

    /**
     * Start the actual scenario, requires background(context) to have been called.
     * Usage: Scenario.&lt;Context&gt;of(description)
     *
     * @param description
     * @return
     * @param <Context>
     */
    static public <Context> Scenario<Context> of(String description) {
        Scenario<Context> scenario = (Scenario<Context>) background.get();
        if (scenario == null) {
            throw new IllegalStateException("Background(context) was not called prior to this method");
        }
        background.set(null);
        scenario.description = description;
        return scenario;
    }

    public String description() {
        return description;
    }

    public Context context() {
        return context;
    }

    @Override
    public GivenAPI<Context> given(Given<Context> given) {
        context = given.run(context);
        return this;
    }
    @Override
    public GivenAPI<Context> and(Given<Context> given) {
        context = given.run(context);
        return this;
    }
    @Override
    public GivenAPI<Context> but(Given<Context> given) {
        context = given.run(context);
        return this;
    }

    @Override
    public WhenAPI<Context> when(When<Context> when) {
        context = when.run(context);
        return this;
    }
    @Override
    public WhenAPI<Context> and(When<Context> when) {
        context = when.run(context);
        return this;
    }
    @Override
    public WhenAPI<Context> but(When<Context> when) {
        context = when.run(context);
        return this;
    }

    @Override
    public ThenAPI<Context> then(Then<Context> then) {
        context = then.run(context);
        return this;
    }
    @Override
    public ThenAPI<Context> and(Then<Context> then) {
        context = then.run(context);
        return this;
    }
    @Override
    public ThenAPI<Context> but(Then<Context> then) {
        context = then.run(context);
        return this;
    }
}
