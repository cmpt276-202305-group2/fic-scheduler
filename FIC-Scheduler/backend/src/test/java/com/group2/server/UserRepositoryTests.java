package com.group2.server;

import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Role;
import com.group2.server.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Optional;

@SpringBootTest
@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test // Save the user to the repository and verify that it was saved correctly
    public void testFindByUsername() {
        ApplicationUser user = new ApplicationUser();
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        var roles = new HashSet<Role>();
        roles.add(Role.ADMIN);
        user.setAuthorities(roles);

        user = userRepository.save(user);

        Optional<ApplicationUser> retrievedUser = userRepository.findByUsername("testUsername");

        assertThat(retrievedUser.isPresent()).isTrue();
        assertThat(retrievedUser.get().getId()).isEqualTo(user.getId());
        assertThat(retrievedUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(retrievedUser.get().getPassword()).isEqualTo(user.getPassword());
        assertThat(retrievedUser.get().getAuthorities()).isEqualTo(user.getAuthorities());
    }

    @Test // Try to retrieve a user that doesn't exist and verify that the returned user
          // is null
    public void testFindByUsernameNonExisting() {
        Optional<ApplicationUser> retrievedUser = userRepository.findByUsername("nonExistingUsername");
        assertThat(retrievedUser.isPresent()).isFalse();
    }

}
