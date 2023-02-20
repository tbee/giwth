package org.tbee.giwth;

public interface ThenAPI<Context> extends BaseAPI<Context> {
    ThenAPI<Context> and(Then<Context> then);
    ThenAPI<Context> but(Then<Context> then);
}
