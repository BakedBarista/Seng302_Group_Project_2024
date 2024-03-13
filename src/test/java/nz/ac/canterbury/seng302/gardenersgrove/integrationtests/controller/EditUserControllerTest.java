package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = EditUserController.class)
@AutoConfigureMockMvc
public class EditUserControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenUserService gardenUserService;



    @BeforeEach
    void setup() {
        EditUserController editUserController = new EditUserController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(editUserController).build();
   }



   // simple test when no deatils are supplied a 400 is created 
   @Test
   public void allFieldsNullTest() {
        boolean noLname = false;
       try {
           mockMvc.perform(post("/users/edit")
                   .param("fname", (String) null)
                   .param("lname", (String) null)
                   .param("noLname", String.valueOf(noLname))
                   .param("email", (String) null)
                   .param("address", (String) null)
                   .param("dob", (String) null)).andExpect(
                    status().is(400)
            );
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }

}