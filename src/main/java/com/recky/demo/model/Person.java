package com.recky.demo.model;

import java.util.UUID;

public class Person {
    private final UUID id;
    private final String name;

    public Person(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person with name  = " + this.name + " and id  = " + this.id;
    }
}
