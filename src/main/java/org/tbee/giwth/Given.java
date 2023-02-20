package org.tbee.giwth;

@FunctionalInterface
public interface Given<Context> {
    Context run(Context context);
}
