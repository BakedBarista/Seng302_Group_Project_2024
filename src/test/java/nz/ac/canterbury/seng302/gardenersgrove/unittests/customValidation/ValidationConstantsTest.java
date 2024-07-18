package nz.ac.canterbury.seng302.gardenersgrove.unittests.customValidation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.customValidation.ValidationConstants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ValidationConstantsTest {
    @Test
    public void testAllValid() {
        String fname = "Iiam";
        String lname = "Innogen";
        String email = "test@uclive.ac.nz";
        String password = "Pa$$w0rd";

        boolean firstNameResult = fname.matches(ValidationConstants.NAME_REGEX);
        boolean lastNameResult = lname.matches(ValidationConstants.NAME_REGEX);
        boolean emailResult = email.matches(ValidationConstants.EMAIL_REGEX);
        boolean passwordResult = password.matches(ValidationConstants.PASSWORD_REGEX);

        assertTrue(firstNameResult);
        assertTrue(lastNameResult);
        assertTrue(emailResult);
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

    @ParameterizedTest
    @ValueSource(strings =
            {"invalid--email@gmail.com",
                    "-invalid-email@gmail.com",
                    "-@gmail.com", "--@gmail.com",
                    "invalid@-invalid.com",
                    "invalid@invalid-.com",
                    "invalid@-.com",
                    "@doe.nz",
                    "jane@doe",
                    "jane.doe.nz",
                    "jane.nz",
                    "jane@.",
                    "jane@",
                    ".@.",
                    "*@.nz",
                    "jane@.nz",
                    "plainaddress",
                    "@missingusername.com",
                    "invalid@.com",
                    "invalid@com",
                    "invalid@missingtld.c",
                    "invalid@-domain.com",
                    "invalid@domain..com",
                    "invalid@domain_.com",
                    "invalid@domain.com.",
                    "invalid@domain-.com",
                    "user..name@domain.com",
                    "user.name@domain..com",
                    "invalid@domain,com",
                    "invalid@domain@domain.com",
                    "invalid@domain@domain",
                    "invalid@domain.c_m",
                    "invalid@domain.c",
                    ".invalid@domain.com",
                    "invalid.@domain.com",
                    "invalid@-domain-.com",
                    "invalid@domain.-com",
                    "invalid@domain.com..",
                    "invalid@domain.com.123",
                    "invalid@.domain.com",
                    "invalid@domain.c_m",
                    "invalid@domain.",
                    "invalid@domaincom",
                    "invalid@domain,com",
                    "invalid@domain.com.123",
                    "invalid@123.456.789.012",
                    "invalid@domain.123",
                    "invalid@domain.12",
                    "123@.com",
                    "123@domain,com",
                    "123@domain.com.",
                    "123@-domain.com",
                    "123@domain-.com",
                    "123@domain..com",
                    "123@domain.c",
                    "123@domain..com",
                    "123@domain,com",
                    "user@domain@domain,.com",
                    "user@domain@domain.com-",
                    "user@domain@domain,com",
                    "user@domain@domain.com.",
                    "user@domain@domain..com",
                    "user@domain@domain@domain@domain@domain@domain@domain@domain-.com"
            })
    void testListOfInvalidEmails(String input) {
        boolean emailResult = input.matches(ValidationConstants.EMAIL_REGEX);
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
