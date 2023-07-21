package org.tbee.giwth.steps;

import org.tbee.giwth.Given;
import org.tbee.giwth.table.TableProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class User {


    public User firstName(String v) {
        System.out.println(this + ".firstName=" + v);
        return this;
    }
    public User lastName(String v) {
        System.out.println(this + ".lastName=" + v);
        return this;
    }

    public User age(int v) {
        System.out.println(this + ".age=" + v);
        return this;
    }
}
