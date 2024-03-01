package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
//simple validation checks for user registration data

public class UserRegoValidation {

    //simple validation checks for name registration data
    public static boolean userNameValidation(String fname, String lname, boolean noLname){
        if (fname.matches("^[a-zA-Z\\s'-]*$") && lname.matches("^[a-zA-Z\\s'-]*$")) { // makeing sure it matches to only alphebet
            if(fname.length() <  65 && lname.length() <  65){ 
                if (noLname && fname.length() > 0) {
                    return lname.length() == 0;
                }
                else {
                    return fname.length() > 0 && lname.length() > 0;
                }
            }
        }
        return false;
    }

    //validation for user email
    public static boolean userEmailValidation(String email){
        if( email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$") ){
            return true;
        }

        return false;
    }

    //validation for checking if the user is over 13 years of age
    public static boolean userYoungDateValidation(String date){
        LocalDate dob = LocalDate.parse(date);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();

        if ( age >= 13){
            return true;
        }
        return false;
    }

    //validation for checking if the user is under 120 years of age
    public static boolean userOldDateValidation(String date){
        LocalDate dob = LocalDate.parse(date);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();
        if ( age < 121){
            return true;
        }
        return false;
    }

    //validation for checking if the password matches confirm password
    public static boolean userPasswordMatchValidation(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    //validation for checking if the password is a strong password
    public static boolean userPasswordStrengthValidation(String password){
        if(password.length() >= 8){ // checking is it contains number special charcters etc
            if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")){ 
                return true;
            }
        }
        return false;
    }
}