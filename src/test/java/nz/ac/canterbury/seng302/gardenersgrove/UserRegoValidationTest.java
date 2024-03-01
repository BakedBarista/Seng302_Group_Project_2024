package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenUserService;
import nz.ac.canterbury.seng302.gardenersgrove.validation.UserRegoValidation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRegoValidationTest {
    private UserRegoValidation userRego;

    @BeforeEach
    public void setUp() {
        userRego = new UserRegoValidation();
    }

    @Test
    public void testRegoValidation() {
        String fname = "Iiam";
        String lname = "Innogen";
        boolean noLname = false;
        String email = "test@uclive.ac.nz";
        String password = "Pa$$w0rd";
        String dob = "2004-12-10";

        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        boolean emailResult = userRego.userEmailValidation(email);
        boolean oldDateResult = userRego.userOldDateValidation(dob);
        boolean youngDateResult = userRego.userYoungDateValidation(dob);
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);

        assertTrue(nameResult);
        assertTrue(emailResult);
        assertTrue(oldDateResult);
        assertTrue(youngDateResult);
        assertTrue(passwordResult);
    }

    @Test
    public void testWrongFirstName() {
        String fname = "1Iiam";
        String lname = "Innogen";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testWrongLastName() {
        String fname = "Iiam";
        String lname = "1Innogen";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testEmptyFirstName() {
        String fname = "";
        String lname = "Innogen";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testTooLongFirstName() {
        String fname = "bfhadfhehfgehdfghdjafhdegrhjfhewhfgehsfgwehfgwhegdwgfdhewgfhdshdha";
        String lname = "Innogen";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testTooLongLastName() {
        String fname = "Iiam";
        String lname = "bfhadfhehfgehdfghdjafhdegrhjfhewhfgehsfgwehfgwhegdwgfdhewgfhdshdha";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testEmptyLastName() {
        String fname = "Iiam";
        String lname = "";
        boolean noLname = false;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertFalse(nameResult);
    }

    @Test
    public void testEmptyLastNameNoLname() {
        String fname = "Iiam";
        String lname = "";
        boolean noLname = true;
        boolean nameResult = userRego.userNameValidation(fname, lname, noLname);
        assertTrue(nameResult);
    }

    @Test
    public void testWrongStartEmail() {
        String email = "@uclive.ac.nz";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    @Test
    public void testWrongEndEmail() {
        String email = "test@uclive";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    @Test
    public void testNoSymbolEmail() {
        String email = "testuclive.ac.nz";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    @Test
    public void testEmptyEmail() {
        String email = "";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    @Test
    public void testTooYoungAge() {
        String dob = "2023-12-10";
        boolean youngDateResult = userRego.userYoungDateValidation(dob);
        assertFalse(youngDateResult);
    }

    @Test
    public void testTooOldAge() {
        String dob = "1902-12-10";
        boolean oldDateResult = userRego.userOldDateValidation(dob);
        assertFalse(oldDateResult);
    }

    @Test
    public void testInvalidDate() {
        String date = "200/200/200";
        assertFalse(true);
    }

    @Test
    public void testTooShortPassword() {
        String password = "1@Pp";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoNumberPassword() {
        String password = "TestP@ssword";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoSymbolPassword() {
        String password = "TestPassw0rd";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoCapitalPassword() {
        String password = "testp@ssw0rd";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoMatchPasswords() {
        String password1 = "TestP@ssword";
        String password2 = "testPasswordDifferent";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }

    @Test
    public void testEmptyPassword1() {
        String password1 = "";
        String password2 = "TestP@ssword";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }

    @Test
    public void testEmptyPassword2() {
        String password1 = "TestP@ssword";
        String password2 = "";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }
}