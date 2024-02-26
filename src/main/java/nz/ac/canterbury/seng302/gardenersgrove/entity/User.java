package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fname;

    @Column(nullable = true)
    private String lname;

    @Column(nullable = false)
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
    protected User() {}

    /**
     * Creates a new FormResult object
     * @param fname first name of user
     * @param lname last name of user 
     * @param email email of user 
     * @param address address of user 
     * @param password password of user 
     * @param DOB date of birth of use 
     */
    public User(String fname, String lname, String email, String address, String password, String DOB) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.address = address;
        this.password = password;
        this.DOB = DOB;
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

    public String getPassword() {
        return password;
    }

    public String getDOB() {
        return DOB;
    }
}
