package ru.solution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.solution.models.Owner;
import ru.solution.repository.OwnerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OwnerControllerTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDatabase")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setupObjectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    @BeforeEach
    void setup() {
        ownerRepository.deleteAll();

        jdbcTemplate.execute("ALTER SEQUENCE owners_owner_id_seq RESTART WITH 1");

        ownerRepository.save(new Owner(1L, "Иван Иванов", LocalDate.parse("1985-05-15"), new ArrayList<>()));
        ownerRepository.save(new Owner(2L, "Петр Петров", LocalDate.parse("1990-08-25"), new ArrayList<>()));
    }

    @Test
    @SneakyThrows
    void findAllOwners() {
        mvc.perform(get("/api/v1/owners"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    void saveOwner() {
        Owner newOwner = new Owner(3L, "Сергей Сергеев", LocalDate.parse("1975-12-01"), new ArrayList<>());
        String json = objectMapper.writeValueAsString(newOwner);

        mvc.perform(post("/api/v1/owners/save_owner")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Сергей Сергеев"));
    }

    @Test
    @SneakyThrows
    void saveOwners() {
        List<Owner> newOwners = List.of(
                new Owner(3L, "Анна Аннова", LocalDate.parse("1988-03-14"), new ArrayList<>()),
                new Owner(4L, "Мария Марова", LocalDate.parse("1992-07-19"), new ArrayList<>())
        );
        String json = objectMapper.writeValueAsString(newOwners);

        mvc.perform(post("/api/v1/owners/save_owners")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    void findOwner() {
        mvc.perform(get("/api/v1/owners/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Иван Иванов"));
    }

    @Test
    @SneakyThrows
    void updateOwner() {
        Owner updatedOwner = new Owner(1L, "Иван Петров", LocalDate.parse("1985-05-15"), new ArrayList<>());
        String json = objectMapper.writeValueAsString(updatedOwner);

        mvc.perform(put("/api/v1/owners/update_owner")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Иван Петров"));
    }

    @Test
    @SneakyThrows
    void deleteOwner() {
        mvc.perform(delete("/api/v1/owners/delete_owner/1"))
                .andExpect(status().is2xxSuccessful());

        assertThat(ownerRepository.existsById(1L)).isFalse();
    }
}
