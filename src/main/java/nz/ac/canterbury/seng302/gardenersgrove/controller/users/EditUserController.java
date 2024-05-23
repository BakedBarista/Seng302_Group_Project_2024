package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;

/**
 * Controller for editing an existing user
 */
@Controller
public class EditUserController {

    private Logger logger = LoggerFactory.getLogger(EditUserController.class);

    private GardenUserService userService;
    private EmailSenderService emailSenderService;

    public EditUserController(GardenUserService userService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Shows the edit user form
     *
     * @param authentication authentication object representing the current user
     * @param model          Thymeleaf model
     * @return The edit user template view
     */
    @GetMapping("/users/edit")
    public String edit(Authentication authentication, Model model) {
        logger.info("GET /users/edit");

        Long userId = (Long) authentication.getPrincipal();
        GardenUser user = userService.getUserById(userId);
        model.addAttribute("userId", userId);

        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setFname(user.getFname());
        editUserDTO.setLname(user.getLname());
        editUserDTO.setEmail(user.getEmail());
        if (user.getDOB() != null) {
            editUserDTO.setDateOfBirth(user.getDOB().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        model.addAttribute("editUserDTO", editUserDTO);

        return "users/editTemplate";
    }

    /**
     * Handles the submission of user edits
     *
     * @param fname          user's current first name
     * @param lname          user's current last name
     * @param noLname        True if the user has no last name
     * @param email          user's current email
     * @param dob            user's current date of birth
     * @param authentication authentication object representing the current user
     * @param model          the Thymeleaf model
     * @return The view name for the edit user template or a redirect URL
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @Valid @ModelAttribute("user") EditUserDTO editUserDTO,
            BindingResult bindingResult,
            Authentication authentication,
            @RequestParam(value = "dateError", required = false) String dateValidity,
            Model model) {
        logger.info("POST /users/edit");

        Long userId = (Long) authentication.getPrincipal();

        GardenUser user = userService.getUserById(userId);
        String currentEmail = user.getEmail();

        if (Objects.equals(dateValidity, "dateInvalid")) {
            bindingResult.rejectValue(
                    "dateOfBirth",
                    "dateOfBirth.formatError",
                    "Date is not in valid format, DD/MM/YYYY, or does not represent a real date"
            );
        }

        if (!editUserDTO.getEmail().equalsIgnoreCase(currentEmail)
                && userService.getUserByEmail(editUserDTO.getEmail()) != null) {
            bindingResult.rejectValue("email", null, "This email address is already in use");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("editUserDTO", editUserDTO);
            return "users/editTemplate";
        }

        user.setFname(editUserDTO.getFname());
        user.setLname(editUserDTO.getLname());
        user.setEmail(editUserDTO.getEmail());
        if (editUserDTO.getDateOfBirth() != null && !editUserDTO.getDateOfBirth().isEmpty()) {
            try {
                user.setDOB(LocalDate.parse(editUserDTO.getDateOfBirth()));
            } catch (DateTimeParseException e) {
                // shouldn't happen because of validation
                logger.info("cannot parse invalid date format");
            }
        } else {
            user.setDOB(null);
        }
        userService.addUser(user);

        return "redirect:/users/user";

    }

    /**
     * Shows the user the form
     */
    @GetMapping("/users/edit/password")
    public String editPassword(Model model) {
        logger.info("GET /users/edit/password");

        EditPasswordDTO editPasswordDTO = new EditPasswordDTO();
        model.addAttribute("editPasswordDTO", editPasswordDTO);

        return "users/editPassword";
    }

    /**
     * Handles submission of password edits
     *
     * @param oldPassword     user's current password
     * @param newPassword     user's new password
     * @param confirmPassword confirmation of the new password
     * @param model           Thymeleaf model
     * @return edit user template
     */
    @PostMapping("/users/edit/password")
    public String submitPassword(
            @Valid @ModelAttribute("editPasswordDTO") EditPasswordDTO editPasswordDTO,
            BindingResult bindingResult,
            Authentication authentication, Model model) {
        logger.info("POST /users/edit/password");

        long id = (long) authentication.getPrincipal();

        GardenUser user = userService.getUserById(id);

        String oldPassword = editPasswordDTO.getOldPassword();
        String newPassword = editPasswordDTO.getNewPassword();
        String confirmPassword = editPasswordDTO.getConfirmPassword();

        if (!user.checkPassword(oldPassword)) {
            bindingResult.rejectValue("oldPassword", null, "Your old password is incorrect");
        }

        if (!newPassword.equals(confirmPassword)) {
            bindingResult.rejectValue("confirmPassword", null, "The new passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            logger.error("Error in password change form: {}", bindingResult.getAllErrors());
            model.addAttribute("editPasswordDTO", editPasswordDTO);

            return "users/editPassword";
        }

        user.setPassword(newPassword);
        userService.addUser(user);
        emailSenderService.sendEmail(user, "Password Changed", "Your password has been updated");
        return "redirect:/users/user";

    }

    /**
     * Handles the submission of profile picture edits
     *
     * @param authentication authentication object representing the current user
     * @param file           the MultipartFile containing the new profile picture
     * @param referer        the referer header value
     * @return A redirect URL
     * @throws IOException
     */
    @PostMapping("/users/profile-picture")
    public String editProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(HttpHeaders.REFERER) String referer) throws IOException {
        logger.info("POST /users/profile-picture");

        Long userId = (Long) authentication.getPrincipal();

        userService.setProfilePicture(userId, file.getContentType(), file.getBytes());

        return "redirect:" + referer;
    }

}
