package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants.*;

import jakarta.validation.constraints.Pattern;

/**
 * Data transfer object for the edit password form
 */
public class EditPasswordDTO {
    private String oldPassword;

    @Pattern(regexp = PASSWORD_REGEX, message = "Your password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number and one special character")
    private String newPassword;

    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
