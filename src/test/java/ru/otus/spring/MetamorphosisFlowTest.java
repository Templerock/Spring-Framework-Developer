package ru.otus.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.otus.spring.domain.Caterpillar;
import ru.otus.spring.integration.MetamorphosisGateway;

@SpringBootTest
@AutoConfigureMockMvc
class MetamorphosisFlowTest {

    @Autowired
    private MetamorphosisGateway gateway;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dayButterflyTransformation() {
        String result = gateway.transform(new Caterpillar("Гусеница-неугомон", "Vanessa", 42));

        assertThat(result)
                .contains("Vanessa")
                .contains("оранжево-чёрная")
                .contains("днём");
    }

    @Test
    void nightButterflyTransformation() {
        String result = gateway.transform(new Caterpillar("Тихий мотылёк", "Sphinx", 17));

        assertThat(result)
                .contains("Sphinx")
                .contains("тускло-серая")
                .contains("ночью");
    }

    @Test
    void skinnyCaterpillarIsRejected() {
        assertThat(gateway.transform(new Caterpillar("Голодная сирота", "Vanessa", 3))).isNull();
    }

    @Test
    void restEndpointTransformsCaterpillar() throws Exception {
        mockMvc.perform(post("/api/metamorphosis")
                        .contentType("application/json")
                        .content("""
                                {"name": "Большая симпатяга", "species": "Aglais", "leavesEaten": 25}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsString("Aglais")));
    }
}
