package ru.solution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.solution.dtos.JwtRequest;
import ru.solution.dtos.RegistrationOwnerDto;
import ru.solution.models.Cat;
import ru.solution.repository.CatRepository;
import ru.solution.repository.OwnerRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SecurityTests {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testDatabase")
            .withUsername("username")
            .withPassword("password")
            .withInitScript("0.1.0.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CatRepository catRepository;

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
        catRepository.deleteAll();

        jdbcTemplate.execute("ALTER SEQUENCE cats_cat_id_seq RESTART WITH 1");

        catRepository.save(new Cat(1L, "Барсик", LocalDate.parse("2020-09-11"), "Перс", "Black", ownerRepository.findByName("user").get(), new ArrayList<>()));
        catRepository.save(new Cat(2L, "Мурзик", LocalDate.parse("2022-03-30"), "Мейн-кун", "White", ownerRepository.findByName("admin").get(), new ArrayList<>()));
    }

    @Test
    void registerUser() throws Exception {
        RegistrationOwnerDto regOwner = new RegistrationOwnerDto();
        regOwner.setName("Артур");
        regOwner.setPassword("123123");
        regOwner.setConfirmPassword("123123");
        regOwner.setBirthDate(LocalDate.parse("2000-10-30"));

        mvc.perform(post("/api/v1/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(regOwner)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void authenticateUser() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        assertNotNull(token);
    }

    @Test
    void badCredentials() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("wrond_password");


        mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authorizedAccessProtectedResource() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        mvc.perform(get("/api/v1/cats")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void unauthorizedAccessProtectedResource() throws Exception {
        mvc.perform(get("/api/v1/cats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userAccessAdminResource() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        mvc.perform(get("/api/v1/owners")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccessAdminResource() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        mvc.perform(get("/api/v1/owners")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void userSaveCat() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        Cat cat = new Cat(3L, "Мартин", LocalDate.parse("2022-09-25"), "Мейн-кун", "Black", null, new ArrayList<>());

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/cats/save_cat")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk());
    }

    @Test
    void adminAccessAllCats() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        mvc.perform(get("/api/v1/cats")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void userAccessOwnedCats() throws Exception {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("password");

        MvcResult mvcResult = mvc.perform(post("/api/v1/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").textValue();

        mvc.perform(get("/api/v1/cats")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}

