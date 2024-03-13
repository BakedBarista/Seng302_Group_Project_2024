package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;

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

    /**
     * JPA required no-args constructor
     */
    protected GardenUser() {}

    /**
     * Creates a new GardenUser object
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

    public List<GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public void setID(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    /**
     * Setter for the user's first name
     * @param fname the user's first name
     */
    public void setFname(String fname) {
        this.fname = fname;
    }
    public String getFname() {
        return fname;
    }

    /**
     * Setter for the user's last name
     * @param lname the user's last name
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLname() {
        return lname;
    }


    /**
     * Setter for the user's email
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the user's date of birth
     * @param DOB the user's date of birth
     */
    public void setDOB(String DOB) {
        this.DOB = DOB;
    }
    public String getDOB() {
        return DOB;
    }

    public void setPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    public boolean checkPassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, this.password);
    }

    public String getProfilePictureContentType() {
        return profilePictureContentType;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String contentType, byte[] profilePicture) {
        this.profilePictureContentType = contentType;
        this.profilePicture = profilePicture;
    }
}
