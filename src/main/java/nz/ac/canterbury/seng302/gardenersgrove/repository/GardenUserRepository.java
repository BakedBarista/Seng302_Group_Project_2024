package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * GardenUser repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface GardenUserRepository extends CrudRepository<GardenUser, Long> {
    /**
     * Retrieves a GardenUser by its ID.
     *
     * @param id The ID of the GardenUser to retrieve
     * @return An Optional containing the GardenUser if found, or empty if not found
     */
    Optional<GardenUser> findById(long id);

    /**
     * Retrieves all GardenUsers.
     *
     * @return A list of all GardenUsers
     */
    List<GardenUser> findAll();

    /**
     * Retrieves a GardenUser by its email address.
     *
     * @param email The email address of the GardenUser to retrieve
     * @return An Optional containing the GardenUser if found, or empty if not found
     */
    @Query("SELECT u FROM GardenUser u WHERE u.email = ?1")
    Optional<GardenUser> findByEmail(String email);

    @Query("SELECT p FROM GardenUser p WHERE ((LOWER(p.fname)=LOWER(?1) and LOWER(p.lname)=LOWER(?2)) or LOWER(p.email)=LOWER(?1)) and p.id!=?3")
    List<GardenUser> findBySearch(String first, String last, Long currentUserId);

    @Query("SELECT p FROM GardenUser p WHERE (LOWER(p.fname)=LOWER(?1) and p.lname is null) and p.id!=?2")
    List<GardenUser> findBySearchNoLname(String first, Long currentUserId);
    
    @Query("SELECT p FROM GardenUser p WHERE ((LOWER(p.fname)=LOWER(?1) and LOWER(p.lname)=LOWER(?2)) or LOWER(p.email)=LOWER(?1)) and p.id=?3")
    Optional<GardenUser> findBySearchMe(String first, String last, Long currentUserId);

    @Query("SELECT p FROM GardenUser p WHERE (LOWER(p.fname)=LOWER(?1) and p.lname is null)  and p.id=?2")
    Optional<GardenUser> findBySearchMeNoLname(String first, Long currentUserId);

    /**
     * Deletes all GardenUsers with email verifiation tokens whose expiry date/time
     * has passed.
     *
     * @param now The current time
     */
    @Modifying
    @Query("DELETE FROM GardenUser u WHERE u.emailValidationTokenExpiryInstant < ?1")
    int deleteUsersWithExpiredEmailTokens(Instant now);

    /**
     * Removes all expired reset password tokens from users
     *
     * @param now The current time
     */
    @Modifying
    @Query("UPDATE GardenUser u SET u.resetPasswordToken = null, u.resetPasswordTokenExpiryInstant = null WHERE u.resetPasswordTokenExpiryInstant < ?1")
    int removeExpiredResetPasswordTokens(Instant now);
}

