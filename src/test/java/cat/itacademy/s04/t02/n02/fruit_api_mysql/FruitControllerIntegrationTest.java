package cat.itacademy.s04.t02.n02.fruit_api_mysql;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.ProviderRepository;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.service.FruitService;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.service.ProviderService;
import org.junit.jupiter.api.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
@Transactional
class FruitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FruitRepository fruitRepository;

    @Autowired
    private FruitService fruitService;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProviderService providerService;

    private Long createTestProvider() throws Exception {
        ProviderRequestDto provider = new ProviderRequestDto();
        MvcResult result = mockMvc.perform(post("/providers")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(provider)))
                .andReturn();


}
