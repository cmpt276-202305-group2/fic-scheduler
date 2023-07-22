package com.group2.server.dto;

import lombok.*;
import java.util.*;

import com.group2.server.model.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NonNull
    private Optional<Integer> id;
    @NonNull
    private Optional<String> username;
    @NonNull
    private Optional<String> password;
    @NonNull
    private Optional<List<String>> roles;
    @NonNull
    private Optional<String> fullName;

    public static ArrayList<String> applicationUserRolesToDtoRoles(Set<Role> roles) {
        var strRoles = new ArrayList<String>();
        for (var role : roles) {
            strRoles.add(role.getAuthority());
        }
        strRoles.sort(Comparator.naturalOrder());
        return strRoles;
    }
}
