package org.tbee.giwth;

public class Scenario<Context> implements GivenAPI<Context>, WhenAPI<Context>, ThenAPI<Context> {

    private Context context;
    public Scenario(String description, Context context) {
        this.context = context;
    }

    static public <Context> Scenario<Context> of(String description, Context context) {
        return new Scenario<Context>(description, context);
    }

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
