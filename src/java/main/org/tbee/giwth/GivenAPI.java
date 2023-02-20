package org.tbee.giwth;

public interface GivenAPI<Context> {
    GivenAPI<Context> and(Given<Context> given);

    WhenAPI<Context> when(When<Context> when);
}
