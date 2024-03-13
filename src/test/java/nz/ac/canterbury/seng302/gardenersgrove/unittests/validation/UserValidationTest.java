package nz.ac.canterbury.seng302.gardenersgrove.unittests.validation;

import static org.junit.jupiter.api.Assertions.*;

import nz.ac.canterbury.seng302.gardenersgrove.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserValidationTest {
    private UserValidation userRego;

    @BeforeEach
    public void setUp() {
        userRego = new UserValidation();
    }

    /**
     * Unit test validating all validation methods
     * All return true
     */
    @Test
    public void testRegoValidation() {
        String fname = "Iiam";
        String lname = "Innogen";
        boolean noLname = false;
        String email = "test@uclive.ac.nz";
        String password = "Pa$$w0rd";
        String dob = "10/12/2004";

        boolean firstNameResult = userRego.userFirstNameValidation(fname);
        boolean lastNameResult = userRego.userLastNameValidation(lname, noLname);

        boolean emailResult = userRego.userEmailValidation(email);
        boolean oldDateResult = userRego.userOldDateValidation(dob);
        boolean youngDateResult = userRego.userYoungDateValidation(dob);
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);

        assertTrue(firstNameResult);
        assertTrue(lastNameResult);
        assertTrue(emailResult);
        assertTrue(oldDateResult);
        assertTrue(youngDateResult);
        assertTrue(passwordResult);
    }

    /**
     * Test passing an invalid first name to the userNameValidation function
     * Should return false
     */
    @Test
    public void testWrongFirstName() {
        String fname = "1Iiam";
        boolean nameResult = userRego.userFirstNameValidation(fname);
        assertFalse(nameResult);
    }

    /**
     * Test passing an invalid last name to the userNameValidation function
     * Should return false
     */
    @Test
    public void testWrongLastName() {
        String lname = "1Innogen";
        boolean noLname = false;
        boolean nameResult = userRego.userLastNameValidation(lname, noLname);
        assertFalse(nameResult);
    }

    /**
     * Test passing an empty first name to the userNameValidation function
     * Should return false
     */
    @Test
    public void testEmptyFirstName() {
        String fname = "";
        boolean nameResult = userRego.userFirstNameValidation(fname);
        assertFalse(nameResult);
    }

    /**
     * Test passing a too long first name to the userNameValidation function
     * Should return true as the name length is validated separately the controlelr
     */
    @Test
    public void testTooLongFirstName() {
        String fname = "bfhadfhehfgehdfghdjafhdegrhjfhewhfgehsfgwehfgwhegdwgfdhewgfhdshdha";
        boolean nameResult = userRego.userFirstNameValidation(fname);
        assertTrue(nameResult);
    }

    /**
     * Test passing a too long last name to the userNameValidation function
     * Should return true as the name length is validated separately the controlelr
     */
    @Test
    public void testTooLongLastName() {
        String lname = "bfhadfhehfgehdfghdjafhdegrhjfhewhfgehsfgwehfgwhegdwgfdhewgfhdshdha";
        boolean noLname = false;
        boolean nameResult = userRego.userLastNameValidation(lname, noLname);
        assertTrue(nameResult);
    }

    /**
     * Test passing an empty last name to the userNameValidation function
     * Should return false
     */
    @Test
    public void testEmptyLastName() {
        String lname = "";
        boolean noLname = false;
        boolean nameResult = userRego.userLastNameValidation(lname, noLname);
        assertFalse(nameResult);
    }

    /**
     * Test passing an empty last name to the userNameValidation
     * function but the "no last name" box has been ticked
     * Should return true
     */
    @Test
    public void testEmptyLastNameNoLname() {
        String lname = "";
        boolean noLname = true;
        boolean nameResult = userRego.userLastNameValidation(lname, noLname);
        assertTrue(nameResult);
    }

    /**
     * Test passing an email with no text before the '@' symbol to userEmailValidation
     * Should return false
     */
    @Test
    public void testWrongStartEmail() {
        String email = "@uclive.ac.nz";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    /**
     * Test passing an invalid email format to userEmailValidation
     * Should return false
     */
    @Test
    public void testWrongEndEmail() {
        String email = "test@uclive";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    /**
     * Test passing an email with no '@' symbol to userEmailValidation
     * Should return false
     */
    @Test
    public void testNoSymbolEmail() {
        String email = "testuclive.ac.nz";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    /**
     * Test passing an empty email to userEmailValidation
     * Should return false
     */
    @Test
    public void testEmptyEmail() {
        String email = "";
        boolean emailResult = userRego.userEmailValidation(email);
        assertFalse(emailResult);
    }

    /**
     * Test passing a birth date younger than 13 years to userYoungDateValidation
     * Should return false
     */
    @Test
    public void testTooYoungAge() {
        String dob = "10/12/2023";
        boolean youngDateResult = userRego.userYoungDateValidation(dob);
        assertFalse(youngDateResult);
    }

    /**
     * Test passing a birth date older than 120 years to userOldDateValidation
     * Should return false
     */
    @Test
    public void testTooOldAge() {
        String dob = "10/12/1902";
        boolean oldDateResult = userRego.userOldDateValidation(dob);
        assertFalse(oldDateResult);
    }

    /**
     * Test passing an invalid date to userInvalidDateValidation
     * Should return false
     */
    @Test
    public void testInvalidDate() {
        String date = "200/200/200";
        boolean invalidDateResult = userRego.userInvalidDateValidation(date);
        assertFalse(invalidDateResult);
    }

    /**
     * Test passing a null date to userInvalidDateValidation since birth date is optional
     * Should return true
     */
    @Test
    public void testEmptyDate() {
        String date = null;
        boolean emptyDateResult = userRego.userInvalidDateValidation(date);
        assertTrue(emptyDateResult);
    }

    /**
     * Test passing a password that is too short to userPasswordStrengthValidation
     * Should return false
     */
    @Test
    public void testTooShortPassword() {
        String password = "1@Pp";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    /**
     * Test passing a password that has no number to userPasswordStrengthValidation
     * Should return false
     */
    @Test
    public void testNoNumberPassword() {
        String password = "TestP@ssword";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    /**
     * Test passing a password that has no symbol to userPasswordStrengthValidation
     * Should return false
     */
    @Test
    public void testNoSymbolPassword() {
        String password = "TestPassw0rd";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    /**
     * Test passing a password that has no capital letter to userPasswordStrengthValidation
     * Should return false
     */
    @Test
    public void testNoCapitalPassword() {
        String password = "testp@ssw0rd";
        boolean passwordResult = userRego.userPasswordStrengthValidation(password);
        assertFalse(passwordResult);
    }

    /**
     * Test passing passwords that do not match to userPasswordMatchValidation
     * Should return false
     */
    @Test
    public void testNoMatchPasswords() {
        String password1 = "TestP@ssword";
        String password2 = "testPasswordDifferent";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }

    /**
     * Test passing an empty first password to userPasswordMatchValidation
     * Should return false
     */
    @Test
    public void testEmptyPassword1() {
        String password1 = "";
        String password2 = "TestP@ssword";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }

    /**
     * Test passing an empty second password to userPasswordMatchValidation
     * Should return false
     */
    @Test
    public void testEmptyPassword2() {
        String password1 = "TestP@ssword";
        String password2 = "";
        boolean passwordResult = userRego.userPasswordMatchValidation(password1, password2);
        assertFalse(passwordResult);
    }
}