package nz.ac.canterbury.seng302.gardenersgrove.controller.users;

import jakarta.validation.Valid;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditPasswordDTO;
import nz.ac.canterbury.seng302.gardenersgrove.entity.dto.EditUserDTO;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailSenderService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;


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
        if (editUserDTO.getLname() == null) {
            editUserDTO.setNoLname(true);
        }
        editUserDTO.setEmail(user.getEmail());
        if (user.getDateOfBirth() != null) {
            editUserDTO.setDateOfBirth(user.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        model.addAttribute("editUserDTO", editUserDTO);

        return "users/editTemplate";
    }

    /**
     * Handles the submission of user edit
     * @param authentication authentication object representing the current user
     * @param model          the Thymeleaf model
     * @return The view name for the edit user template or a redirect URL
     */
    @PostMapping("/users/edit")
    public String submitUser(
            @Valid @ModelAttribute("editUserDTO") EditUserDTO editUserDTO,
            BindingResult bindingResult,
            @RequestParam("image") MultipartFile file,
            Authentication authentication,
            @RequestParam(value = "dateError", required = false) String dateValidity,
            Model model) throws IOException {
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
        if (editUserDTO.isNoLname()) {
            editUserDTO.setLname(null);
        } else if ((editUserDTO.getLname() == null || editUserDTO.getLname().isBlank())) {
            bindingResult.rejectValue("lname", null, "Last name cannot be empty");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("editUserDTO", editUserDTO);
            model.addAttribute("userId", userId);
            return "users/editTemplate";
        }

        try {
            editProfilePicture(userId, file);
        } catch (IOException e) {
            throw e;
        }

        user.setFname(editUserDTO.getFname());
        user.setLname(editUserDTO.getLname());
        user.setEmail(editUserDTO.getEmail());
        if (editUserDTO.getDateOfBirth() != null && !editUserDTO.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(LocalDate.parse(editUserDTO.getDateOfBirth()));
                logger.info("" + user.getDateOfBirth());
            } catch (DateTimeParseException e) {
                // shouldn't happen because of validation
                logger.info("cannot parse invalid date format");
            }
        } else {
            user.setDateOfBirth(null);
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
     * @param userId id of the user to update picture
     * @param file the MultipartFile containing the new profile picture
     * @throws IOException
     */
    
    public void editProfilePicture(Long userId, MultipartFile file) throws IOException{
        logger.info("POST /users/profile-picture");
        if(file.getSize() != 0){
            userService.setProfilePicture(userId, file.getContentType(), file.getBytes());
        }
    }

}
