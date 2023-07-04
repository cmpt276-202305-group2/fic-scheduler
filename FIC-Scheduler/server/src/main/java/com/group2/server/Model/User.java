package com.group2.server.Model;


import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//this will provide getters and setters for the fields and the constructor
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    //userDetials is an interface that provides core user information which ig
    //it is helpful for authentication and authorization

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    //this is the method that is used to get the authorities of the user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        //this will return the role of the user (admin /prof / coordinator)
        return List.of(new SimpleGrantedAuthority(role.name()));
        
    }

    @Override
    public String getUsername() {
       return username;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
         return true;
     }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
            return true;
    }
}
