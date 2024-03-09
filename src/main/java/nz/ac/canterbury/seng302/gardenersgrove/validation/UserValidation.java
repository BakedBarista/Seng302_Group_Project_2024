package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * simple validation checks for user registration data
 */
public class UserValidation {

    public static boolean userFirstNameValidation(String fname) {
        String acceptedNameRegex = "^[a-zA-Z\\s'-]*$";
        int maxNameLength = 64;
        boolean validFirstName = fname.matches(acceptedNameRegex) && fname.length() <= maxNameLength && !fname.isEmpty();

        return validFirstName;
    }

    public static boolean userLastNameValidation(String lname, boolean noLname) {
        String acceptedNameRegex = "^[a-zA-Z\\s'-]*$";
        int maxNameLength = 64;
        boolean validLastName = ( noLname || (lname.matches(acceptedNameRegex) && lname.length() <= maxNameLength && !lname.isEmpty()) );

        return validLastName;
    }


    /**
     * validation for user email
     * @param email
     * @return bool
     */
    public static boolean userEmailValidation(String email){
        if( email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$") ){
            return true;
        }

        return false;
    }

    /**
     * validation for checking if the user is over 13 years of age
     * @param date
     * @return bool
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
     * validation for checking if the user is under 120 years of age
     * @param date
     * @return bool
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
     * Validation for checking if the user has input a valid date string
     * @param date
     * @return bool
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
     * validation for checking if the password matches confirm password
     * @param password
     * @param confirmPassword
     * @return bool
     */
    public static boolean userPasswordMatchValidation(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    /**
     * validation for checking if the password is a strong password
     * @param password
     * @return bool
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