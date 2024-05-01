package nz.ac.canterbury.seng302.gardenersgrove.entity.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants.*;

public class ResetPasswordDTO {
    @Pattern(regexp = EMAIL_REGEX, message = "Email address must be in the form ‘jane@doe.nz’")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

