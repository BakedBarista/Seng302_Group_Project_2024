package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.users.EditUserController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

public class U7UpdatePasswordFeature {
    private static GardenUserRepository userRepository;
    private static EmailSenderService emailSenderService;
    private static BindingResult bindingResult;
    private static Model model;
    private static Authentication authentication;

    private static GardenUserService userService;
    private static EditUserController editUserController;

    private GardenUser user;
    private EditPasswordDTO editPasswordDTO;

    @BeforeAll
    public static void beforeAll() {
        userRepository = mock(GardenUserRepository.class);
        emailSenderService = mock(EmailSenderService.class);
        bindingResult = mock(BindingResult.class);
        model = mock(Model.class);
        authentication = mock(Authentication.class);

        userService = new GardenUserService(userRepository);
        editUserController = new EditUserController(userService, emailSenderService);
    }

    @Given("I am on the change password form")
    public void i_am_on_the_change_password_form() {
        user = U2LogInFeature.user;
        editPasswordDTO = new EditPasswordDTO();
    }

    @When("I enter my old password {string}")
    public void i_enter_my_current_password(String oldPassword) {
        editPasswordDTO.setOldPassword(oldPassword);
    }

    @When("I enter a new password {string}")
    public void i_enter_a_new_password(String newPassword) {
        editPasswordDTO.setNewPassword(newPassword);
        editPasswordDTO.setConfirmPassword(newPassword);
    }

    @When("I submit the change password form")
    public void i_submit_the_change_password_form() {
        when(authentication.getPrincipal()).thenReturn((Long) 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        editUserController.submitPassword(editPasswordDTO, bindingResult, authentication, model);
    }

    @Then("My password is updated to {string}")
    public void my_password_is_updated_to(String expectedPassword) {
        assertTrue(user.checkPassword(expectedPassword));
    }

    @Then("An email is sent to my inbox to confirm that my password has been changed")
    public void an_email_is_sent_to_my_inbox_to_confirm_that_my_password_has_been_changed() {
        verify(emailSenderService).sendEmail(eq(user), eq("Password Changed"), any());
    }
}
