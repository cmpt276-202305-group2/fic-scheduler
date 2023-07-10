package com.group2.server.Services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group2.server.Model.ApplicationUser;
import com.group2.server.Model.Role;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("In the user details service");
        
        if(!username.equals("peyman")) throw new UsernameNotFoundException("you're not peyman"); 

        Set<Role> roles = new HashSet<Role>();
        roles.add(new Role(1, "USER"));

        return new ApplicationUser(1, "peyman", encoder.encode("123"), roles);
    }
    
}
