package org.techfrog.todo.config;

import org.techfrog.todo.repository.TodoRepository;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@EnableCassandraRepositories(basePackageClasses = TodoRepository.class)
public class CassandraConfig {
}