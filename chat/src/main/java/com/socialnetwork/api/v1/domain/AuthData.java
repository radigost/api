package com.socialnetwork.api.v1.domain;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AuthData {
    private String username;

    private String password;
}
