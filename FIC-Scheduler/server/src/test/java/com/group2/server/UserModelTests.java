package com.group2.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.group2.server.Model.User;
import org.junit.jupiter.api.Test;

public class UserModelTests {

    @Test
    public void testUserSettersAndGetters() {
        User user = new User();

        // Set the values
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setRole("testRole");

        // Check that the getters return the correct values
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testUsername");
        assertThat(user.getPassword()).isEqualTo("testPassword");
        assertThat(user.getRole()).isEqualTo("testRole");
    }

    @Test
    public void testUserConstructor() {
        User user = new User("testUsername", "testPassword", "testRole");

        // Check that the getters return the correct values
        assertThat(user.getUsername()).isEqualTo("testUsername");
        assertThat(user.getPassword()).isEqualTo("testPassword");
        assertThat(user.getRole()).isEqualTo("testRole");
    }
}
