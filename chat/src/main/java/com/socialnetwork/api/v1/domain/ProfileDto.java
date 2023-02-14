package com.socialnetwork.api.v1.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    int id;

    String firstName;

    String lastName;

    int yearsOld;

    String gender;

    String interests;

    String city;

    int ownerId;
}
