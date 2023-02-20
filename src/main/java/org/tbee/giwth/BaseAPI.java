package org.tbee.giwth;

public interface BaseAPI<Context> {
    BaseAPI<Context> given(Given<Context> given);
    WhenAPI<Context> when(When<Context> when);
    ThenAPI<Context> then(Then<Context> then);
}
