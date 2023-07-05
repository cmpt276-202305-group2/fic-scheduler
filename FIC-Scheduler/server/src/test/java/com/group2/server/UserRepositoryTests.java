package com.group2.server;

import com.group2.server.Model.User;
import com.group2.server.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {
    
    @Autowired
    private UserRepository userRepository;

    @Test // Save the user to the repository and verify that it was saved correctly 
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setRole("admin");

        userRepository.save(user);

        User retrievedUser = userRepository.findByUsername("testUsername");

        assertThat(retrievedUser).isEqualTo(user);
    }

    @Test // Try to retrieve a user that doesn't exist and verify that the returned user is null
    public void testFindByUsernameNonExisting() {

    User retrievedUser = userRepository.findByUsername("nonExistingUsername");
    assertThat(retrievedUser).isNull();
}

}
