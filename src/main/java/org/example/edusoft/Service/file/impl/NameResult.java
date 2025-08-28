package org.example.edusoft.service.file.impl;

public class NameResult {
    private final String name;
    private final int count;

    public NameResult(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
