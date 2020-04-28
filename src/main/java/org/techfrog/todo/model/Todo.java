package org.techfrog.todo.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Table
public class Todo {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    @Column
    private String task;

    @Column
    private Set<String> tags = new HashSet<>();

    public Todo() {
    }

    public Todo(String id, String task) {
        this.id = id;
        this.task = task;
    }

    public Todo(String task) {
        this.task = task;
    }

    public Todo(String task, Set<String> tags) {
        this.task = task;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id.equals(todo.id) &&
                task.equals(todo.task) &&
                Objects.equals(tags, todo.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, task, tags);
    }
}
