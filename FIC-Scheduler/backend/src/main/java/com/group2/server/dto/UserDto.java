package com.group2.server.dto;

import lombok.*;
import java.util.*;

import com.group2.server.model.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private List<String> roles;
    private String fullName;

    public static ArrayList<String> applicationUserRolesToDtoRoles(Set<Role> roles) {
        var strRoles = new ArrayList<String>();
        for (var role : roles) {
            strRoles.add(role.getAuthority());
        }
        strRoles.sort(Comparator.naturalOrder());
        return strRoles;
    }
}
