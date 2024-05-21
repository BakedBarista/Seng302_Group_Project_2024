package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    private LocalDate DOB;

    @Column(nullable = true)
    private String profilePictureContentType;

    @Column(nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] profilePicture;

    // these are a set of friendships in the friends table where the user is sender
    @OneToMany(mappedBy = "sender")
    private Set<Friends> friendshipsAsSender = new HashSet<>();

    // these are a set of friendships in the friends table where the user is receiver
    @OneToMany(mappedBy = "receiver")
    private Set<Friends> friendshipsAsReceiver = new HashSet<>();


    @Column(nullable = true)
    private String emailValidationToken;

    @Column(nullable = true)
    private Instant emailValidationTokenExpiryInstant;

    @Column(nullable = true)
    private String resetPasswordToken;

    @Column(nullable = true)
    private Instant resetPasswordTokenExpiryInstant;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int strikeCount = 0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean accountDisabled = false;

    @Column(nullable = true)
    private Instant accountDisabledExpiryInstant;

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
    public GardenUser(String fname, String lname, String email, String password, LocalDate DOB) {
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
     * Constructs the user's full name from their first and last names
     *
     * @return the user's full name
     */
    public String getFullName() {
        if (lname != null) {
            return fname + " " + lname;
        } else {
            return fname;
        }
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
    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

    /**
     * Retrieves user's date of birth
     *
     * @return user's date of birth
     */
    public LocalDate getDOB() {
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
     *  - may be null
     */
    public void setEmailValidationToken(String emailValidationToken) {
        this.emailValidationToken = emailValidationToken;
    }


    /**
     * Get this users emailValidationToken
     *  - may be null
     * @return emailValidationToken
     */
    public String getEmailValidationToken() {
        return emailValidationToken;
    }

    /**
     * Get this users resetPasswordToken
     *  - may be null
     * @return emailValidationToken
     */
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    /**
     * Set the resetPasswordToken of this user
     */
    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
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
     * @return emailValidationTokenExpiryInstant
     */
    public Instant getEmailValidationTokenExpiryInstant() {
        return this.emailValidationTokenExpiryInstant;
    }

    /**
     * Set the resetPasswordTokenExpiryInstant of this user
     */
    public void setResetPasswordTokenExpiryInstant(Instant timeInstance) {
        this.resetPasswordTokenExpiryInstant = timeInstance;
    }

    /**
     * Return the resetPasswordTokenExpiryInstant of this user
     *  - may be null
     * @return resetPasswordTokenExpiryInstant
     */
    public Instant getResetPasswordTokenExpiryInstant() {
        return this.resetPasswordTokenExpiryInstant;
    }

    /**
     * Sets the id of the user. This method only intended for use in tests.
     * 
     * @param id the id of the user
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the strike count of the user
     *
     * @return the number of strikes the user has accumulated
     */
    public int getStrikeCount() {
        return strikeCount;
    }

    /**
     * Sets the strike count of the user
     * 
     * @param strikeCount the number of strikes the user has accumulated
     */
    public void setStrikeCount(int strikeCount) {
        this.strikeCount = strikeCount;
    }

    /**
     * Checks if the user's account is disabled
     *
     * @return true if the account is disabled, otherwise false
     */
    public boolean isAccountDisabled() {
        return accountDisabled;
    }

    /**
     * Sets the accountDisabled status of the user
     *
     * @param accountDisabled true if the account is disabled, otherwise false
     */
    public void setAccountDisabled(boolean accountDisabled) {
        this.accountDisabled = accountDisabled;
    }

    /**
     * Gets the time at which the user's account will be re-enabled, if there is one
     * 
     * @return the time at which the user's account will be re-enabled
     */
    public Instant getAccountDisabledExpiryInstant() {
        return accountDisabledExpiryInstant;
    }

    /**
     * Sets the time at which the user's account will be re-enabled, or null if the
     * account should not be automatically re-enabled
     *
     * @param accountDisabledExpiryInstant the time at which the user's account will
     *                                     be re-enabled
     */
    public void setAccountDisabledExpiryInstant(Instant accountDisabledExpiryInstant) {
        this.accountDisabledExpiryInstant = accountDisabledExpiryInstant;
    }
}
