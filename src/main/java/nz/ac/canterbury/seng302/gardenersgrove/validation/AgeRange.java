package nz.ac.canterbury.seng302.gardenersgrove.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Constraint(validatedBy = AgeRange.Validator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AgeRange.List.class)
public @interface AgeRange {

    int minAge() default 0;

    int maxAge() default Integer.MAX_VALUE;

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        AgeRange[] value();
    }

    static class Validator implements ConstraintValidator<AgeRange, String> {

        private int minAge;
        private int maxAge;

        @Override
        public void initialize(AgeRange constraintAnnotation) {
            this.minAge = constraintAnnotation.minAge();
            this.maxAge = constraintAnnotation.maxAge();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            LocalDate dob;
            try {
                dob = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                return true;
            }

            

            Clock clock = context.getClockProvider().getClock();
            LocalDate now = clock.instant().atZone(clock.getZone()).toLocalDate();

            int age = Period.between(dob, now).getYears();
            
            //Age cannot be less than zero
            age = Math.max(age,0);

            return minAge <= age && age <= maxAge;
        }
    }
}
