package org.techfrog.todo.controller;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.test.context.ActiveProfiles;
import org.techfrog.todo.MainApplication;
import org.techfrog.todo.helper.FileLoader;
import org.techfrog.todo.model.Todo;
import org.techfrog.todo.repository.TodoRepository;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("embedded")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MainApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoEmbeddedAcceptanceTest {

    @LocalServerPort
    private int port;
    private String url;
    @Autowired
    private TodoRepository todoRepository;

    @Test
    public void getTodos() {
        Todo todo = new Todo("test testcontainers");
        todoRepository.save(todo);

        List<Todo> response = get(url).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath()
                .getList(".", Todo.class);

        assertEquals(1, response.size());
        assertEquals(todo.getTask(), response.get(0).getTask());
    }

    @Test
    public void createTodo() {
        Map<String, String> request = new HashMap<>();
        String id = UUID.randomUUID().toString();
        request.put("id", id);
        request.put("task", "test testcontainers");

        String reponseId = given().contentType("application/json")
                .body(request)
                .when()
                .post(url)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");

        assertEquals(id, reponseId);
    }

    @PostConstruct
    public void init() {
        this.url = String.format("http://localhost:%s/", port);
    }

    @BeforeAll
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder()
                .addContactPoints("127.0.0.1").withPort(9142)
                .withoutJMXReporting().build();
        Session session = cluster.connect();
        session.execute(FileLoader.read("classpath:init/initial-seed.cql"));
        Thread.sleep(5000);
    }

    @AfterEach
    public void after() {
        todoRepository.deleteAll();
    }

    @AfterAll
    public static void stopCassandraEmbedded() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

}
