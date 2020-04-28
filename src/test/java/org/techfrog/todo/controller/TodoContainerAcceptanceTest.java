package org.techfrog.todo.controller;

import org.springframework.test.context.ActiveProfiles;
import org.techfrog.todo.model.Todo;
import org.techfrog.todo.repository.TodoRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.CassandraContainer;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("container")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TodoContainerAcceptanceTest.Initializer.class)
public class TodoContainerAcceptanceTest {

    @LocalServerPort
    private int port;

    private String url;

    @Autowired
    private TodoRepository todoRepository;

    @PostConstruct
    public void init() {
        this.url = String.format("http://localhost:%s/", port);
    }

    @ClassRule
    public static CassandraContainer cassandraContainer = new CassandraContainer("cassandra:3.11")
        .withInitScript("init/initial-seed.cql");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configContext) {
            TestPropertyValues.of(
                    "spring.data.cassandra.contact-points=" + cassandraContainer.getContainerIpAddress(),
                    "spring.data.cassandra.port=" + cassandraContainer.getMappedPort(9042)
            ).applyTo(configContext);
        }
    }

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

    @AfterEach
    public void after() {
        todoRepository.deleteAll();
    }

    @BeforeAll
    public static void setUp() {
        cassandraContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        cassandraContainer.stop();
    }
}