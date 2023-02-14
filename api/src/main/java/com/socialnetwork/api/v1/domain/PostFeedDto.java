package com.socialnetwork.api.v1.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostFeedDto {
    Integer profileId;

    String text;

    LocalDateTime creationDate;
}
