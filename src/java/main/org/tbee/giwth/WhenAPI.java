package org.tbee.giwth;

public interface WhenAPI<Context> {
    WhenAPI<Context> and(When<Context> when);

    ThenAPI<Context> then(Then<Context> then);
}
