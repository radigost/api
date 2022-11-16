package com.socialnetwork.api.v1.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class MessageDto {
    private String id;

    private String text;

    private String timestamp;
}
