package org.tbee.giwth;

public interface ThenAPI<Context> {
    ThenAPI<Context> and(Then<Context> then);
}
