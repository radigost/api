package com.socialnetwork.api.v1.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWithPasswordDto {

    int id;

    String username;

    String password;

    String role;
}
