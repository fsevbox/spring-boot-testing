package org.techfrog.todo.service;

import org.techfrog.todo.model.Todo;
import org.techfrog.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo getTodo(String id) {
        return todoRepository.findById(id).orElse(null);
    }
}
