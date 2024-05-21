package nz.ac.canterbury.seng302.gardenersgrove.unittests.customValidation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants;

public class ValidationConstantsTest {
    @Test
    public void testAllValid() {
        String fname = "Iiam";
        String lname = "Innogen";
        String email = "test@uclive.ac.nz";
        String password = "Pa$$w0rd";
//        String dob = "10/12/2004";

        boolean firstNameResult = fname.matches(ValidationConstants.NAME_REGEX);
        boolean lastNameResult = lname.matches(ValidationConstants.NAME_REGEX);

        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
//        boolean dateResult = dob.matches(ValidationConstants.DATE_REGEX);
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);

        assertTrue(firstNameResult);
        assertTrue(lastNameResult);
        assertTrue(emailResult);
//        assertTrue(dateResult);
        assertTrue(passwordResult);
    }

    @Test
    public void testWrongName() {
        String fname = "1Iiam";
        boolean nameResult = fname.matches(ValidationConstants.NAME_REGEX);
        assertFalse(nameResult);
    }

    @Test
    public void testNonAsciiODiaeresis() {
        String fname = "Weiß-Köhler";
        boolean nameResult = fname.matches(ValidationConstants.NAME_REGEX);
        assertTrue(nameResult);
    }

    @Test
    public void testFirstNonAsciiAMacron() {
        String fname = "Tāne";
        boolean nameResult = fname.matches(ValidationConstants.NAME_REGEX);
        assertTrue(nameResult);
    }

    @Test
    public void testFirstNonAsciiChineseZi() {
        String fname = "字";
        boolean nameResult = fname.matches(ValidationConstants.NAME_REGEX);
        assertTrue(nameResult);
    }


    @Test
    public void testWrongStartEmail() {
        String email = "@uclive.ac.nz";
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        assertFalse(emailResult);
    }

    @Test
    public void testWrongEndEmail() {
        String email = "test@uclive";
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        assertFalse(emailResult);
    }

    @Test
    public void testNoSymbolEmail() {
        String email = "testuclive.ac.nz";
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        assertFalse(emailResult);
    }

    @Test
    public void testEmptyEmail() {
        String email = "";
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        assertFalse(emailResult);
    }

    @Test
    public void testAmpersandInEmail() {
        String email = "&&&@gmail.com";
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        assertFalse(emailResult);
    }

    @Test
    public void testInvalidDate() {
        String date = "200/200/200";
        boolean invalidDateResult = date.matches(ValidationConstants.DATE_REGEX);
        assertFalse(invalidDateResult);
    }

    @Test
    public void testTooShortPassword() {
        String password = "1@Pp";
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoNumberPassword() {
        String password = "TestP@ssword";
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoSymbolPassword() {
        String password = "TestPassw0rd";
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);
        assertFalse(passwordResult);
    }

    @Test
    public void testNoCapitalPassword() {
        String password = "testp@ssw0rd";
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);
        assertFalse(passwordResult);
    }
}
