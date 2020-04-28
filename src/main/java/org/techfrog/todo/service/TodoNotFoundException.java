package org.techfrog.todo.service;

import java.util.UUID;

public class TodoNotFoundException extends RuntimeException {
    private String id;

    public TodoNotFoundException(String id) {
        super();
        this.id = id;
    }
}
