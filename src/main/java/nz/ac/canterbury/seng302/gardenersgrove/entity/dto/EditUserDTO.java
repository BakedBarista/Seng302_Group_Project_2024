package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nz.ac.canterbury.seng302.gardenersgrove.validation.AgeRange;
import nz.ac.canterbury.seng302.gardenersgrove.validation.ValidDate;

import static nz.ac.canterbury.seng302.gardenersgrove.validation.ValidationConstants.*;

/**
 * Data transfer object for the edit user form
 */
public class EditUserDTO {
    @NotBlank(message = "First name cannot be empty")
    @Pattern(regexp = NAME_REGEX, message = "First name must only include letters, spaces, hyphens or apostrophes")
    @Size(min = 0, max = 64, message = "First Name must be 64 characters long or less.")
    @Column(nullable = false)
    private String fname;

    @Pattern(regexp = NAME_REGEX, message = "Last name must only include letters, spaces, hyphens or apostrophes")
    @Size(min = 0, max = 64, message = "Last Name must be 64 characters long or less.")
    @Column(nullable = true)
    private String lname;

    private boolean noLname;

    @Pattern(regexp = EMAIL_REGEX, message = "Email address must be in the form ‘jane@doe.nz’")
    private String email;

    @ValidDate()
    @AgeRange(minAge = USER_MIN_AGE, message = "You must be 13 years or older to create an account")
    @AgeRange(maxAge = USER_MAX_AGE, message = "The maximum age allowed is 120 years")
    private String dateOfBirth;

    private String description;

    private byte[] banner;

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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDescription() {return description;}
    public void setDescription(String description) { this.description = description;}

}
