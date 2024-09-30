package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.PublicProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouritePlantsController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("integration-tests")
@AutoConfigureMockMvc
class FavouritePlantControllerMVCTests {
    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenUserRepository userRepository;

    @Autowired
    private FavouritePlantsController favouritePlantController;

    @Autowired
    private GardenRepository gardenRepository;



    private GardenUser user;
    @Autowired
    private FriendsRepository friendRepository;

    @BeforeEach
    void setup() throws JsonProcessingException {
        String dateString = " 2001-01-01";
        LocalDate localDate = LocalDate.parse(dateString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);

        user = userRepository.save(new GardenUser("John", "Doe", "postTester@gmail.com",  "Password1!", localDate));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList()));



        // Create MVC
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(favouritePlantController)
                .build();
    }

    @Transactional
    @AfterEach
    void cleanup() {
        userRepository.findAll().forEach(user -> {
            gardenRepository.deleteByOwnerId(user.getId());
            friendRepository.deleteBySenderId(user.getId());
        });
        userRepository.deleteAll();
    }

    @Test
    void testSearchPlants_whenNoMatchingPlant_thenEmptyResponse() throws Exception {
        mockMvc.perform(post("/users/edit-public-profile/search")
                        .param("search", "tomato")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void whenPlantFavourited_thenUpdateFavouritePlants() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("ids", List.of(1L, 2L, 3L)));

        mockMvc.perform(put("/users/edit-public-profile/favourite-plant")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }



}


