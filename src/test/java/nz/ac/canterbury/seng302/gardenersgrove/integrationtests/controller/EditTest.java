package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = EditUserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class EditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenUserService gardenUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private GardenUser gardenUser;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @BeforeEach
    public void setup() {
        gardenUser = new GardenUser("$2Jane", "Dough", "jane@email.com", "home",
                "P#ssw0rd", "01/01/1998");
    }

    @Test
    public void EditController_EditUser_ReturnEdited() throws Exception {
        // Mocking the behavior of userService.addUser to return the input gardenUser
        given(gardenUserService.addUser(any())).willReturn(gardenUser);

        // Mocking the behavior of authentication.getPrincipal() to return a Long value
        given(authentication.getPrincipal()).willReturn(1L);

        // Performing a POST request to the controller endpoint
        ResultActions response = mockMvc.perform(post("/users/edit")
                .param("fname", "Maxzi")
                .param("lname", "Francisco")
                .param("noLname", "false")
                .param("email", "maxzi@gmail.com")
                .param("address", "home")
                .param("id", "1L")
                .param("dob", "10/10/2000")
                .param("profilePictureContentType", "jpg")
                .param("profilePicture", "null")
                .principal(authentication));

        // Verifying that the response status is 201 (created)
        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
