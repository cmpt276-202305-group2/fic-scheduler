package com.group2.server.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, INSTRUCTOR, COORDINATOR;

    @Override
    public String getAuthority() {
        return this.toString();
    }

}
