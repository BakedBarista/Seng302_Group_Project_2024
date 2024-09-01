package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;// Test class for the controller
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.gardens.FavouriteGardenController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FriendsRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("integration-tests")
@AutoConfigureMockMvc
class FavouriteGardenControllerMVCTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GardenUserRepository userRepository;

    @Autowired
    FavouriteGardenController favouriteGardenController;
    @Autowired
    private FriendsRepository friendRepository;

    @Autowired
    private GardenRepository gardenRepository;

    private GardenUser user;


    @BeforeEach
    void setup() throws JsonProcessingException {
        String dateString = " 2001-01-01";
        LocalDate localDate = LocalDate.parse(dateString.trim(), DateTimeFormatter.ISO_LOCAL_DATE);

        user = userRepository.save(new GardenUser("John", "Doe", "postTester@gmail.com",  "Password1!", localDate));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList()));


        // Create MVC
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(favouriteGardenController)
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
    void testFavouriteGarden() throws Exception {
        mockMvc.perform(post("/users/edit-public-profile/favourite-garden")
                        .param("search", "rose")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateFavouriteGarden() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("id", "1"));

        mockMvc.perform(put("/users/edit-public-profile/favourite-garden")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Favourite Garden Updated"));
    }
}