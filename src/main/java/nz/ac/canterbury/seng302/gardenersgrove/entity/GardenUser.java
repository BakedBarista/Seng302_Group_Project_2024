package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import nz.ac.canterbury.seng302.gardenersgrove.repository.ValidationGroups;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Entity class reflecting an entry of fname, lname, email, password and date of birth(DOB)
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
public class GardenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name cannot be empty", groups = {ValidationGroups.FirstOrder.class})
    @Pattern(regexp = "^[\\p{L}\\s'-]*$", message = "First name must only include letters, spaces,hyphens or apostrophes", groups = {ValidationGroups.SecondOrder.class})
    @Size(min = 0, max = 64, message = "First Name must be 64 characters long or less.", groups = {ValidationGroups.SecondOrder.class})
    @Column(nullable = false)
    private String fname;

    @Pattern(regexp = "^[\\p{L}\\s'-]*$", message = "Last name must only include letters, spaces,hyphens or apostrophes", groups = {ValidationGroups.SecondOrder.class})
    @Size(min = 0, max = 64, message = "Last Name must be 64 characters long or less.", groups = {ValidationGroups.SecondOrder.class})
    @Column(nullable = true)
    private String lname;

    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$", message = "Email address must be in the form ‘jane@doe.nz’", groups = {ValidationGroups.SecondOrder.class})
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;


    @Column(nullable = true)
    private String DOB;

    @Column(nullable = true)
    private String profilePictureContentType;

    @Column(nullable = true)
    @Lob
    private byte[] profilePicture;

    /**
     * JPA required no-args constructor
     */
    public GardenUser() {}

    /**
     * Creates a new GardenUser object
     *
     * @param fname first name of user
     * @param lname last name of user 
     * @param email email of user 
     * @param password password of user 
     * @param DOB date of birth of use 
     */
    public GardenUser(String fname, String lname, String email, String password, String DOB) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.DOB = DOB;

        this.setPassword(password);
    }

    /**
     * Gets the authorities granted to the user
     *
     * @return
     */
    public List<GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Retrieves the id of user
     *
     * @return id of user
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for the user's first name
     *
     * @param fname the user's first name
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * Retrieves the user's first name
     *
     * @return user's first name
     */
    public String getFname() {
        return fname;
    }

    /**
     * Setter for the user's last name
     *
     * @param lname the user's last name
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     * Retrieves user's last name
     *
     * @return user's last name
     */
    public String getLname() {
        return lname;
    }

    /**
     * Setter for the user's email
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves user's email
     *
     * @return user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the user's date of birth
     *
     * @param DOB the user's date of birth
     */
    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    /**
     * Retrieves user's date of birth
     *
     * @return user's date of birth
     */
    public String getDOB() {
        return DOB;
    }

    /**
     * Sets the password of the user
     *
     * @param password
     */
    public void setPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    /**
     * Checks if the provided password matches the user's password
     *
     * @param password password to check
     * @return True if the password matches, otherwise false
     */
    public boolean checkPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, this.password);
    }

    /**
     * Retrieves the content type of the user's profile picture
     *
     * @return The content type of the profile picture
     */
    public String getProfilePictureContentType() {
        return profilePictureContentType;
    }

    /**
     * Retrieves the byte array representing the user's profile picture
     *
     * @return The byte array of the profile picture
     */
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    /**
     * Sets the profile picture of the user
     *
     * @param contentType contentType The content type of the profile picture
     * @param profilePicture rofilePicture The byte array representing the profile picture
     */
    public void setProfilePicture(String contentType, byte[] profilePicture) {
        this.profilePictureContentType = contentType;
        this.profilePicture = profilePicture;
    }
}
