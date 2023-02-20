package org.tbee.giwth;

public interface GivenAPI<Context> extends BaseAPI<Context> {
    GivenAPI<Context> and(Given<Context> given);
    GivenAPI<Context> but(Given<Context> given);
}
