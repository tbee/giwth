package org.tbee.giwth;

public interface WhenAPI<Context> extends BaseAPI<Context> {
    WhenAPI<Context> and(When<Context> when);
    WhenAPI<Context> but(When<Context> when);
}
