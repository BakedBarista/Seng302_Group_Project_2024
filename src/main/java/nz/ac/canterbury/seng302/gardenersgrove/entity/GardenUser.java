package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;

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

    @Column(nullable = false)
    private String fname;

    @Column(nullable = true)
    private String lname;

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

    @Column(nullable = true)
    private String emailValidationToken;

    @Column(nullable = true)
    private Instant emailValidationTokenExpiryInstant;


    /**
     * JPA required no-args constructor
     */
    public GardenUser() {}

    /**
     * Createsimport java.util.HashSet; a new GardenUser object
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
     * Gets the set of friends where user is user 2
     *
     * @return Set<Friends> both their id and the other users id
     */ 
    // public Set<Friends> getFriendshipsAsUser2() {
    //     return friendshipsAsUser2;
    // }

    // /**
    //  * Gets the set of friends where user is user 1
    //  *
    //  * @return Set<Friends> both their id and the other users id
    //  */ 
    // public Set<Friends> getFriendshipsAsUser1() {
    //     return friendshipsAsUser1;
    // }


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

    /**
     * Set the emailValidationToken of this user
     */
    public void setEmailValidationToken(String emailValidationToken) {
        this.emailValidationToken = emailValidationToken;
    }

    /**
     * get this users emailValidationToken
     *  - may be null
     * @return emailValidationToken
     */
    public String getEmailValidationToken() {
        return emailValidationToken;
    }

    /**
     * Set the emailValidationTokenExpiryInstant of this user
     */
    public void setEmailValidationTokenExpiryInstant(Instant timeInstance) {
        this.emailValidationTokenExpiryInstant = timeInstance;
    }

    /**
     * Return the emailValidationTokenExpiryInstant of this user
     *  - may be null
     * @return
     */
    public Instant getEmailValidationTokenExpiryInstant() {
        return this.emailValidationTokenExpiryInstant;
    }

    public void setId(long id) {
        this.id = id;
    }
}
