package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.Pattern;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.PASSWORD_REGEX;

/**
 * Data transfer object for the reset password callback form
 */
public class ResetPasswordCallbackDTO {
    private String token;

    @Pattern(regexp = PASSWORD_REGEX, message = "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String newPassword;

    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
