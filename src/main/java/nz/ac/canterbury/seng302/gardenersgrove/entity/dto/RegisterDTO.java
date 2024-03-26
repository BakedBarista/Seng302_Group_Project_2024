package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object for the register form
 */
public class RegisterDTO {
    @NotBlank(message = "First name cannot be empty")
    @Pattern(regexp = "^[\\p{L}\\s'-]*$", message = "First name must only include letters, spaces, hyphens or apostrophes")
    @Size(min = 0, max = 64, message = "First Name must be 64 characters long or less.")
    @Column(nullable = false)
    private String fname;

    @Pattern(regexp = "^[\\p{L}\\s'-]*$", message = "Last name must only include letters, spaces,hyphens or apostrophes")
    @Size(min = 0, max = 64, message = "Last Name must be 64 characters long or less.")
    @Column(nullable = true)
    private String lname;

    private boolean noLname;

    @Pattern(regexp = ValidationConstants.EMAIL_REGEX, message = "Email address must be in the form ‘jane@doe.nz’")
    private String email;

    @Pattern(regexp = ValidationConstants.PASSWORD_REGEX, message = "Your password must beat least 8 characters long and include at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

    // TODO: Can we check for matching passwords?
    private String confirmPassword;

    // TODO: Validation
    private String DOB;

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
        if (noLname) {
            this.lname = null;
        }
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

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        if (DOB == null || DOB.isEmpty()) {
            this.DOB = null;
        } else {
            this.DOB = DOB;
        }
    }
}
