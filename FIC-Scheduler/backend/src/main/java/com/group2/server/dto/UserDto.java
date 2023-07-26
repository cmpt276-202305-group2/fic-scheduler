package com.group2.server.dto;

import lombok.*;
import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.group2.server.model.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class UserDto implements EntityDto {
    private Integer id;
    private String username;
    private String password;
    private List<String> roles;
    private String fullName;

    public static List<String> applicationUserRolesToDtoRoles(Set<Role> roles) {
        var strRoles = new ArrayList<String>();
        for (Role role : roles) {
            strRoles.add(role.getAuthority());
        }
        strRoles.sort(Comparator.naturalOrder());
        return strRoles;
    }
}
