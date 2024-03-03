package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;

/**
 * Entity class reflecting an entry of fname, lname, email, address, password and date of birth(DOB)
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
    private String address;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String DOB;


    /**
     * JPA required no-args constructor
     */
    protected GardenUser() {}

    /**
     * Creates a new GardenUser object
     * @param fname first name of user
     * @param lname last name of user 
     * @param email email of user 
     * @param address address of user 
     * @param password password of user 
     * @param DOB date of birth of use 
     */
    public GardenUser(String fname, String lname, String email, String address, String password, String DOB) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.address = address;
        this.DOB = DOB;

        this.setPassword(password);
    }

    public List<GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public Long getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
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
}
