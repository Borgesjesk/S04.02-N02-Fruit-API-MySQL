package cat.itacademy.s04.t02.n02.fruit_api_mysql;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.service.FruitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
class ProviderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FruitService fruitService;

    private ProviderResponseDto createTestProvider(String name, String country) throws Exception {
        ProviderRequestDto provider = new ProviderRequestDto(name, country);
        MvcResult result = mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andExpect(status().isCreated()).andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ProviderResponseDto.class);
    }

    @Test
    @DisplayName("POST: Create a valid provider")
    void createProvider() throws Exception {
        ProviderRequestDto provider = new ProviderRequestDto("Transports TDA", "Italy");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(provider)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Transports TDA"));
    }

    @Test
    @DisplayName("GET ALL: Retrieve all providers")
    void getAllProviders() throws Exception {

        createTestProvider("Logistic ABC", "Spain");
        createTestProvider("Logistic DFG", "USA");

        mockMvc.perform(get("/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].name").exists());
    }

    @Test
    @DisplayName("GET BY ID: Find provider by specific ID")
    void getProviderById() throws Exception {
        ProviderResponseDto provider = createTestProvider("Logistic HIJ", "Luxembourg");

        mockMvc.perform(get("/providers/{id}", provider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Logistic HIJ"));
    }

    @Test
    @DisplayName("PUT: Update provider details")
    void updateProvider() throws Exception {
        ProviderResponseDto provider = createTestProvider("Transports ABC ", "Spain");
        ProviderRequestDto updateDto = new ProviderRequestDto("Transports ABC updated", "Germany");

        mockMvc.perform(put("/providers/{id}", provider.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Transports ABC updated"))
                .andExpect(jsonPath("$.country").value("Germany"));
    }

    @Test
    @DisplayName("DELETE: Remove provider with no associated fruits")
    void removeProvider() throws Exception {
        ProviderResponseDto provider = createTestProvider("Logistic ZZZ ", "Russia");

        mockMvc.perform(delete("/providers/{id}", provider.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/providers/{id}", provider.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN an existing provider name WHEN creating a duplicate THEN return 409 Conflict")
    void shouldReturnConflict_whenProviderNameExists() throws Exception {
        ProviderResponseDto provider = createTestProvider("Logistics PRS", "Spain");
        ProviderRequestDto duplicateDto = new ProviderRequestDto("Logistics PRS", "Portugal");

        mockMvc.perform(post("/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GIVEN a provider with active fruits WHEN deleting THEN return 400 Bad Request")
    void shouldPreventDeletionOfProviderWithFruits() throws Exception {

        ProviderResponseDto provider = createTestProvider("Logistic UNI", "Sweden");
        fruitService.create(new FruitRequestDto(provider.getId(), "Orange", 10));

        mockMvc.perform(delete("/providers/{id}", provider.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Integrity Constraint Violation")));
    }

    @Test
    @DisplayName("GIVEN a non-existing ID WHEN searching THEN return 404 Not Found")
    void shouldReturnNotFound_whenProviderIdIsNotValid() throws Exception {

        mockMvc.perform(get("/providers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}