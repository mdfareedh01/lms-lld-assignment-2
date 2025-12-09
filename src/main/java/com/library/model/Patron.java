package com.library.model;

import com.library.model.enums.PatronType;

public class Patron {
    private final String id;
    private final String name;
    private final PatronType type;
    private final String email;

    public Patron(String id, String name, PatronType type, String email) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PatronType getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Patron{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
