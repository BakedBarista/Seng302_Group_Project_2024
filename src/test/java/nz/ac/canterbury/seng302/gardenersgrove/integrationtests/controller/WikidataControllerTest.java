package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.api.WikiDataAPIController;
import nz.ac.canterbury.seng302.gardenersgrove.security.CustomAuthenticationProvider;
import nz.ac.canterbury.seng302.gardenersgrove.service.WikidataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(WikiDataAPIController.class)
public class WikidataControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WikidataService wikidataService;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @BeforeEach
    void setup() {
        Mockito.reset(wikidataService);
        Authentication authentication = new UsernamePasswordAuthenticationToken("jan.doe@gmail.com", "password");
        Authentication authenticatedToken = customAuthenticationProvider.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
    }

    @Test
    void givenPlantExists_whenSearchTomato_thenReturnInformationAndImage() throws Exception {
        String plantInfo = "[{\"label\":\"Tomato\",\"description\":\"A red fruit\",\"id\":\"Q235\",\"image\":\"https://commons.wikimedia.org/wiki/Special:FilePath/Tomato.jpg\"}]";
        Mockito.when(wikidataService.getPlantInfo("tomato")).thenReturn(plantInfo);

        mockMvc.perform(get("/api/searchPlant")
                        .param("search", "tomato")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(plantInfo));
    }

    @Test
    void givenPlantNotExist_whenSearchReturnsNoResults_thenReturnEmptyList() throws Exception {
        String plantInfo = "[]";
        Mockito.when(wikidataService.getPlantInfo("nonexistentplant")).thenReturn(plantInfo);

        mockMvc.perform(get("/api/searchPlant")
                        .param("search", "nonexistentplant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(plantInfo));
    }
}
