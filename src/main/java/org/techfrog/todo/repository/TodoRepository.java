package org.techfrog.todo.repository;

import org.techfrog.todo.model.Todo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends CassandraRepository<Todo, String> {
}
