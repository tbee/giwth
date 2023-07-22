package org.tbee.giwth.steps;

public class User {

    private String firstName;
    public User firstName(String v) {
        firstName = v;
        return this;
    }
    public String firstName() {
        return firstName;
    }

    String lastName;
    public User lastName(String v) {
        lastName = v;
        return this;
    }
    public String lastName() {
        return lastName;
    }

    int age;
    public User age(int v) {
        age = v;
        return this;
    }
    public int age() {
        return age;
    }
}
