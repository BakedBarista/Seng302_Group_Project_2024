package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenUserService gardenUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private GardenUser gardenUser;

    Authentication authentication = mock(Authentication.class);


    @BeforeEach
    public void setup() {
        gardenUser = new GardenUser("Maxzi", "Francisco", "maxzi@gmail.com", "Home", "password", "10/10/2000");

    }

    @Test
    public void UserController_RegisterUser_ReturnRegistered() throws Exception {
        // Mocking the behavior of userService.addUser to return the input gardenUser
        given(gardenUserService.addUser(ArgumentMatchers.any())).willReturn(gardenUser);
        // Performing a POST request to the controller endpoint
        ResultActions response = mockMvc.perform(post("/users/register")
                .param("id", "1L")
                .param("fname", "Maxzi")  // Provide a non-empty value for fname
                .param("lname", "Francisco")
                .param("noLname", "false")  // Provide a value for noLname
                .param("email", "maxzi@gmail.com")
                .param("address", "home")
                .param("dob", "10/10/2000")
                .principal(authentication));

        // Verifying that the response status is 201 (created)
        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

}
