package org.techfrog.todo.controller;


import org.techfrog.todo.model.Todo;
import org.techfrog.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TodoController {

    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping(produces = "application/json")
    public List<Todo> getTodos() {
        return todoService.getTodos();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    @GetMapping("/{id}")
    public Todo one(@PathVariable String id) {
        return todoService.getTodo(id);
    }
}
