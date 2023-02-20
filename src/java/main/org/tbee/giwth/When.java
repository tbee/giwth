package org.tbee.giwth;

@FunctionalInterface
public interface When<Context> {
    Context run(Context context);
}
