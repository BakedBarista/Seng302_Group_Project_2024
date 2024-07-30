package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.validation.AgeRange;
import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidDate;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;

/**
 * Data transfer object for the register form
 */
public class RegisterDTO {
    @NotBlank(message = "First name cannot be empty")
    @Pattern(regexp = NAME_REGEX, message = "First name must only include letters, spaces, hyphens or apostrophes")
    @Size(min = 0, max = NAME_MAX_LEN, message = "First Name must be 64 characters long or less.")
    private String fname;

    @Pattern(regexp = NAME_REGEX, message = "Last name must only include letters, spaces, hyphens or apostrophes")
    @Size(min = 0, max = NAME_MAX_LEN, message = "Last Name must be 64 characters long or less.")
    private String lname;

    private boolean noLname;

    @Pattern(regexp = EMAIL_REGEX, message = "Email address must be in the form ‘jane@doe.nz’")
    @Pattern(regexp = EMAIL_LENGTH_REGEX, message = "Local part(before @) should be less than 64 characters and the domain part(after @) should be less than 255 characters")
    @Size(max = 320, message = "Email address length exceeds max length of 320 characters")
    private String email;

    @Pattern(regexp = PASSWORD_REGEX, message = "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;

    private String confirmPassword;

    @ValidDate()
    @AgeRange(minAge = USER_MIN_AGE, message = "You must be 13 years or older to create an account")
    @AgeRange(maxAge = USER_MAX_AGE, message = "The maximum age allowed is 120 years")
    private String dateOfBirth;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public boolean isNoLname() {
        return noLname;
    }

    public void setNoLname(boolean noLname) {
        this.noLname = noLname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
