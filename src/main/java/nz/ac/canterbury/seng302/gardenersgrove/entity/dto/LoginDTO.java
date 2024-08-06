package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;

import jakarta.validation.constraints.Pattern;

/**
 * Data transfer object for the login form
 */
public class LoginDTO {
    @Pattern(regexp = EMAIL_REGEX, message = "Email address must be in the form ‘jane@doe.nz’")
    private String email;

    private String password;

    /**
     * Gets the email of the user
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user
     *
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password that the user has inputted
     *
     * @return the password that the user has inputted
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password that the user has inputted
     *
     * @param password the password that the user has inputted
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
