package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.ArrayList;

import java.util.List;


@WebMvcTest
public class EditUserControllerTest{

    @Autowired
    private MockMvc mockMvc;

    List<GardenUser> databaseUsers = new ArrayList<>();

    GardenUser mockUser;

    @BeforeEach
    void setup() {

        GardenUserRepository mockGardenUserRepository = Mockito.mock(GardenUserRepository.class);
        GardenUserService gardenUserService = new GardenUserService(mockGardenUserRepository);
        EditUserController editUserController = new EditUserController();


       // For testing getting users by ID
       GardenUser mockUser = new GardenUser("liam", "ceelen", "liam@gmail.com",  "15", "liamspassword1?", "05/05/2003");
       Mockito.when(gardenUserService.getUser()).thenReturn(List.of(mockUser));

       // For testing getting ALL gardens from the database
       Mockito.when(mockGardenUserRepository.findAll()).thenReturn(databaseUsers);


       this.mockMvc = MockMvcBuilders.standaloneSetup(editUserController).build();
   }


    @Test
   public void allFieldsNullTest() {
       try {
           mockMvc.perform(post("/users/edit")
                   .param("fname", (String) null)
                   .param("lname", (String) null)
                   .param("email", (String) null)
                   .param("address", (String) null)
                   .param("email", (String) null)
                   .param("dob", (String) null))
                   .andExpectAll(
                        model().attribute("incorrectFirstName", "First name cannot be empty and must only include letters, spaces,hyphens or apostrophes"),
                        model().attribute("incorrectLastName", "First name cannot be empty and must only include letters, spaces,hyphens or apostrophes"),
                        model().attribute("incorrectEmail", "Email address must be in the form ‘jane@doe.nz’")
                   );
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

}