package cat.itacademy.s04.t02.n02.fruit_api_mysql;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class FruitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private Long createTestProvider(String name, String country) throws Exception {
        ProviderRequestDto provider = new ProviderRequestDto(name, country);
        MvcResult result = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andReturn();

        ProviderResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ProviderResponseDto.class);
        return response.getId();
    }

    private FruitResponseDto createTestFruit(Long providerId, String name, int weightInKilos) throws Exception {
        FruitRequestDto fruit = new FruitRequestDto(providerId, name, weightInKilos);
        MvcResult result = mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruit)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(), FruitResponseDto.class);

    }

    @Test
    @DisplayName("POST: Create a valid fruit linked to provider")
    void createValidFruitLinkedToProvider() throws Exception {
        Long providerId = createTestProvider("Supplier ABC", "Spain");
        FruitRequestDto fruitDto = new FruitRequestDto(providerId, "Banana", 1);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruitDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Banana"))
                .andExpect(jsonPath("$.weightInKilos").value(1))
                .andReturn();
    }

    @Test
    @DisplayName("GET ALL: Retrieve all fruits in isolated state")
    void getAllFruits() throws Exception {
        Long providerId = createTestProvider("General Supplier", "Spain");
        createTestFruit(providerId, "Kiwi", 1);
        createTestFruit(providerId, "Apple", 2);

        mockMvc.perform(get("/fruits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Kiwi')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Apple')]").exists());
    }

    @Test
    @DisplayName("GET BY ID: Find specific fruit")
    void getFruitById() throws Exception {
        Long providerId = createTestProvider("ID test supplier", "Spain");
        FruitResponseDto response = createTestFruit(providerId, "Mango", 4);

        mockMvc.perform(get("/fruits/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Mango"));
    }

    @Test
    @DisplayName("PUT: Update fruit name and weight in Kilos")
    void updateFruitNameAndWeightInKilos() throws Exception {
        Long providerId = createTestProvider("Update Supplier", "Spain");
        FruitResponseDto response = createTestFruit(providerId, "Old Fruit", 4);
        FruitRequestDto updateDto = new FruitRequestDto(providerId, "New Fruit", 10);

        mockMvc.perform(put("/fruits/{id}", response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Fruit"))
                .andExpect(jsonPath("$.weightInKilos").value(10));
    }

    @Test
    @DisplayName("DELETE: Remore fruit and verify 404")
    void deleteFruitNotFound() throws Exception {
        Long providerId = createTestProvider("Delete Supplier", "Spain");
        FruitResponseDto response = createTestFruit(providerId, "Disposable Fruit", 4);

        mockMvc.perform(delete("/fruits/{id}", response.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/fruits/{id}", response.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN an invalid Provider ID WHEN creating fruit THEN return 404")
    void createInvalidProviderId() throws Exception {
        FruitRequestDto fruitDto = new FruitRequestDto(999L, "OOFruit", 3);

        mockMvc.perform(post("/fruits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fruitDto)))
                .andExpect(status().isNotFound());

    }
}