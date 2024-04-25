package nz.ac.canterbury.seng302.gardenersgrove.integrationtests.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenUser;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenUserRepository;



@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private GardenUserRepository gardenUserRepository;

    // test to check that the repository is actually saving a garden user
    @Test
    public void gardenUser_save_returnSaved(){

        GardenUser gardenUser = new GardenUser("liam", "ceelen", "liam@gmai.com", "password", null);

        GardenUser user = gardenUserRepository.save(gardenUser);

        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getId()).isGreaterThan(0);
    }

    //testing that the findall methods returns all users stored in the database
    @Test
    public void gardenUser_findAll_returnMoreThanOneUser(){

        GardenUser liam = new GardenUser("liam", "ceelen", "liam@gmai.com", "password", null);
        GardenUser ben = new GardenUser("ben", "lastname", "ben@gmai.com", "reallylong", null);
        gardenUserRepository.save(liam);
        gardenUserRepository.save(ben);

        List<GardenUser> userList = gardenUserRepository.findAll();
        
        Assertions.assertThat(userList).isNotNull();
        Assertions.assertThat(userList.size()).isEqualTo(3);
    }

    //testing the find by ide method in the repository
    @Test
    public void gardenUser_findById_returnsUser(){

        GardenUser liam = new GardenUser("liam", "ceelen", "liam@gmai.com", "password", null);
        gardenUserRepository.save(liam);

        Optional<GardenUser> userList = gardenUserRepository.findById(liam.getId());
        
        Assertions.assertThat(userList).isNotNull();
    }

    //testing the email query. 
    @Test
    public void gardenUser_findByEmail_returnsUserNotNull(){

        GardenUser liam = new GardenUser("liam", "ceelen", "liam@gmai.com", "password", null);
        gardenUserRepository.save(liam);

        GardenUser userList = gardenUserRepository.findByEmail(liam.getEmail()).get();
        
        Assertions.assertThat(userList).isNotNull();
        Assertions.assertThat(userList).isEqualTo(liam);
    }

}
