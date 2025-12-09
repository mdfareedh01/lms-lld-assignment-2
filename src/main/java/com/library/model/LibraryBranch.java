package com.library.model;

public class LibraryBranch {
    private final String id;
    private final String name;
    private final String address;

    public LibraryBranch(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
