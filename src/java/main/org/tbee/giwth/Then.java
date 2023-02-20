package org.tbee.giwth;

@FunctionalInterface
public interface Then<Context> {
    Context run(Context context);
}
