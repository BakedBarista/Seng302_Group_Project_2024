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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("integration-tests")
@AutoConfigureMockMvc
public class PublicProfileControllerMVCTests {
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

    @Autowired
    private PublicProfileController publicProfileController;

    @BeforeEach
    void setup() throws JsonProcessingException {

        user = userRepository.save(new GardenUser("John", "Doe", "postTester@gmail.com",  "Password1!", null));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getId(), null, Collections.emptyList())
        );

        // Create MVC
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(publicProfileController)
                .build();
    }
    @Transactional
    @AfterEach
    void cleanup() {
        userRepository.findAll().forEach(user -> {gardenRepository.deleteByOwnerId(user.getId());
            friendRepository.deleteBySenderId(user.getId());});userRepository.deleteAll();
    }
    @Test
    void givenUserHasNoDOB_whenGetPublicProfile_thenFlowerSelectionDropdownisDisabled() throws Exception {
        mockMvc.perform(get("/users/edit-public-profile"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", hasProperty("id", is(user.getId()))))
                .andExpect(model().attribute("user", hasProperty("fname", is(user.getFname()))))
                .andExpect(model().attribute("flowers", hasSize(0)));
        }
    }

