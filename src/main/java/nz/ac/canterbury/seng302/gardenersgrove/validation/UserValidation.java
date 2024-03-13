package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * simple validation checks for user registration data
 */
public class UserValidation {

    /**
     * Validates the user's first name.
     *
     * @param fname The first name to be validated.
     * @return True if the first name is valid, false otherwise.
     */
    public static boolean userFirstNameValidation(String fname) {
        String acceptedNameRegex = "^[\\p{L}\\s'-]*$";
        boolean validFirstName = fname.matches(acceptedNameRegex) && !fname.isEmpty();

        return validFirstName;
    }

    /**
     * Validation method aimed at Edit User Profile. Will display when the text box is empty.
     *
     * @param fname The first name to be validated.
     * @return True if the first name is not empty, false otherwise.
     */
    public static boolean userFirstNameEmptyValidation(String fname) {
        return !fname.isEmpty();
    }

    /**
     * Validation method aimed at Edit User Profile. Will display when the first name has the wrong characters.
     *
     * @param fname The first name to be validated.
     * @return True if the first name contains valid characters, false otherwise.
     */
    public static boolean userFirstNameWrongCharactersValidation(String fname) {
        String acceptedNameRegex = "^[\\p{L}\\s'-]*$";
        return fname.matches(acceptedNameRegex);
    }

    /**
     * Validates the user's last name.
     *
     * @param lname The last name to be validated.
     * @param noLname A boolean indicating whether the user has no last name.
     * @return True if the last name is valid, false otherwise.
     */
    public static boolean userLastNameValidation(String lname, boolean noLname) {
        String acceptedNameRegex = "^[\\p{L}\\s'-]*$";
        boolean validLastName = ( noLname || (lname.matches(acceptedNameRegex) && !lname.isEmpty()) );
        return validLastName;
    }

    /**
     * Validation method aimed at Edit User Profile. Will display when the text box is empty.
     *
     * @param lname The last name to be validated.
     * @param noLname A boolean indicating whether the user has no last name.
     * @return True if the last name is not empty, false otherwise.
     */
    public static boolean userLastNameEmptyValidation(String lname, boolean noLname) {
        return ( noLname || !lname.isEmpty() );
    }

    /**
     * Validation method aimed at Edit User Profile. Will display when the last name has the wrong characters.
     *
     * @param lname The last name to be validated.
     * @param noLname A boolean indicating whether the user has no last name.
     * @return True if the last name contains valid characters, false otherwise.
     */
    public static boolean userLastNameWrongCharactersValidation(String lname, boolean noLname) {
        String acceptedNameRegex = "^[\\p{L}\\s'-]*$";
        return ( noLname || lname.matches(acceptedNameRegex) );
    }


    /**
     * Validates the user's email address.
     *
     * @param email The email address to be validated.
     * @return True if the email address is valid, false otherwise.
     */
    public static boolean userEmailValidation(String email){
        if( email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$") ){
            return true;
        }

        return false;
    }

    /**
     * Validates if the user is at least 13 years old.
     *
     * @param date The date of birth of the user.
     * @return True if the user is at least 13 years old, false otherwise.
     */
    public static boolean userYoungDateValidation(String date){
        if (date != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate dob = LocalDate.parse(date, formatter);
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(dob, currentDate);
                int age = period.getYears();
                if (age >= 13) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if the user is under 120 years old.
     *
     * @param date The date of birth of the user.
     * @return True if the user is under 120 years old, false otherwise.
     */
    public static boolean userOldDateValidation(String date){
        if (date != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate dob = LocalDate.parse(date, formatter);
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(dob, currentDate);
                int age = period.getYears();
                if (age < 121) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if the provided date string is in a valid format.
     *
     * @param date The date string to be validated.
     * @return True if the date string is in a valid format, false otherwise.
     */
    public static boolean userInvalidDateValidation(String date) {
        if (date != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                LocalDate.parse(date, formatter);
                return true;
            } catch (Exception e){
                return false;
            }
        }
        return true;
    }

    /**
     * Validates if the password matches the confirm password.
     *
     * @param password The password to be validated.
     * @param confirmPassword The confirm password to be validated.
     * @return True if the password matches the confirm password, false otherwise.
     */
    public static boolean userPasswordMatchValidation(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    /**
     * Validates if the password is strong.
     *
     * @param password The password to be validated.
     * @return True if the password is strong, false otherwise.
     */
    public static boolean userPasswordStrengthValidation(String password){
        if(password.length() >= 8){ // checking is it contains number special charcters etc
            if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")){ 
                return true;
            }
        }
        return false;
    }
}