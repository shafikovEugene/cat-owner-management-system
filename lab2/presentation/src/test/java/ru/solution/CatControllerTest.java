package ru.solution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.solution.controller.CatController;
import ru.solution.models.Cat;
import ru.solution.models.Owner;
import ru.solution.repository.CatRepository;
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
public class CatControllerTest {
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
        ownerRepository.deleteAll();

        jdbcTemplate.execute("ALTER SEQUENCE cats_cat_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE owners_owner_id_seq RESTART WITH 1");

        Owner owner1 = new Owner(1L, "Иван Иванов", LocalDate.parse("1985-05-15"), new ArrayList<>());
        Owner owner2 = new Owner(2L, "Петр Петров", LocalDate.parse("1990-08-25"), new ArrayList<>());

        ownerRepository.save(owner1);
        ownerRepository.save(owner2);

        catRepository.save(new Cat(1L, "Барсик", LocalDate.parse("2020-09-11"), "Перс", "Black", owner1, new ArrayList<>()));
        catRepository.save(new Cat(2L, "Мурзик", LocalDate.parse("2022-03-30"), "Мейн-кун", "White", owner2, new ArrayList<>()));
    }

    @Test
    @SneakyThrows
    void findCat() {
        mvc.perform(get("/api/v1/cats/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Барсик"));
    }

    @Test
    @SneakyThrows
    void findAllCats() {
        mvc.perform(get("/api/v1/cats"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    void saveCat() {
        Cat newCat = new Cat(3L, "Снежок", LocalDate.parse("2021-05-15"), "Сиам", "Grey", null, new ArrayList<>());
        String json = objectMapper.writeValueAsString(newCat);

        mvc.perform(post("/api/v1/cats/save_cat")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Снежок"));
    }

    @Test
    @SneakyThrows
    void saveCats() {
        List<Cat> newCats = List.of(
                new Cat(3L, "Снежок", LocalDate.parse("2021-05-15"), "Сиам", "Grey", null, new ArrayList<>()),
                new Cat(4L, "Тигр", LocalDate.parse("2020-12-01"), "Бенгал", "Orange", null, new ArrayList<>())
        );
        String json = objectMapper.writeValueAsString(newCats);

        mvc.perform(post("/api/v1/cats/save_cats")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    void updateCat() {
        Cat updatedCat = new Cat(1L, "Аврорий", LocalDate.parse("2020-09-11"), "Перс", "Black", null, new ArrayList<>());
        String json = objectMapper.writeValueAsString(updatedCat);

        mvc.perform(put("/api/v1/cats/update_cat")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("Аврорий"));
    }

    @Test
    @SneakyThrows
    void deleteCat() {
        mvc.perform(delete("/api/v1/cats/delete_cat/1"))
                .andExpect(status().is2xxSuccessful());

        assertThat(catRepository.existsById(1L)).isFalse();
    }

    @Test
    @SneakyThrows
    void findCatsByColor() {
        mvc.perform(get("/api/v1/cats/color/Black"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Барсик"));
    }

    @Test
    @SneakyThrows
    void findCatsByOwner() {
        mvc.perform(get("/api/v1/cats/owner/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Барсик"));
    }

    @Test
    @SneakyThrows
    void setOwner() {
        Owner newOwner = ownerRepository.findById(2L).orElseThrow();
        Cat cat = catRepository.findById(1L).orElseThrow();
        CatController.SetOwnerRequest request = new CatController.SetOwnerRequest();
        request.setCat(cat);
        request.setOwner(newOwner);
        String json = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/cats/set_owner")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful());

        assertThat(catRepository.findById(1L).orElseThrow().getOwner().getId()).isEqualTo(2L);
    }

    @Test
    @SneakyThrows
    void makeFriends() {
        Cat cat1 = catRepository.findById(1L).orElseThrow();
        Cat cat2 = catRepository.findById(2L).orElseThrow();
        CatController.MakeFriendsRequest request = new CatController.MakeFriendsRequest();
        request.setCat1(cat1);
        request.setCat2(cat2);
        String json = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/cats/make_friends")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().is2xxSuccessful());

    }
}
