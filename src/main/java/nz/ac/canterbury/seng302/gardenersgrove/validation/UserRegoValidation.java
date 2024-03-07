package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * simple validation checks for user registration data
 */
public class UserRegoValidation {
    /**
     * simple validation checks for name registration data
     * @param fname
     * @param lname
     * @param noLname
     * @return bool
     */
    public static boolean userNameValidation(String fname, String lname, boolean noLname){
        if (fname.matches("^[a-zA-Z\\s'-]*$") && (lname == null || lname.matches("^[a-zA-Z\\s'-]*$"))) {
            if(fname.length() <  65 && lname.length() <  65){ 
                if (noLname && fname.length() > 0) {
                    return lname.length() == 0 || lname == null;
                }
                else {
                    return fname.length() > 0 && lname.length() > 0;
                }
            }
        }
        return false;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD/MM/YYYY");
        LocalDate dob = LocalDate.parse(date, formatter);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();

        if ( age >= 13){
            return true;
        }
        return false;
    }

    /**
     * validation for checking if the user is under 120 years of age
     * @param date
     * @return bool
     */
    public static boolean userOldDateValidation(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD/MM/YYYY");
        LocalDate dob = LocalDate.parse(date, formatter);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();
        if ( age < 121){
            return true;
        }
        return false;
    }

    /**
     * Validation for checking if the user has input a valid date string
     * @param date
     * @return bool
     */
    public static boolean userInvalidDateValidation(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD/MM/YYYY");
            LocalDate.parse(date, formatter);
            return true;
        } catch (Exception e){
            return false;
        }
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