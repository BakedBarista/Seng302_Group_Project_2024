import java.time.LocalDate;
import java.time.Period;
 //simple validation checks for user registration data

public class UserValidation {

    //simple validation checks for name registration data
    static boolean userNameValidation(String fname, String lname){
        if (fname.matches("^[a-zA-Z\\s'-]*$") && fname.matches("^[a-zA-Z\\s'-]*$")) { // makeing sure it matches to only alphebet
            if(fname.length() <  65 && lname.length() <  65){ 
                return true
            }
        }
        return false
    }

    //validation for user email
    static boolean userEmailValidation(String email){
        if( email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$") ){
            return true
        }

        return false
    }

    //validation for checking if the user is over 13 years of age
    static boolean userYoungDateValidation(String date){

        LocalDate dob = LocalDate.parse(date);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();

        if ( age >= 13){
            return true
        }
        return false
    }

    //validation for checking if the user is under 120 years of age
    static boolean userOldDateValidation(String date){

        LocalDate dob = LocalDate.parse(date);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        int age = period.getYears();

        if ( age < 121){
            return true
        }
        return false
    }

    //validation for checking if the password matches confirm password
    static boolean userPasswordMatchValidation(String password, String confirmPassword){
        return (password === confirmPassword);
    }

    //validation for checking if the password is a strong password
    static boolean userPasswordStrengthValidation(String password){
        if(password.length() < 8){ // checking is it contains number special charcters ect
            if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$)")){ 
                return true
            }
        }
        return false
    }




}