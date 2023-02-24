package com.socialnetwork.api.v1.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {

    public static final PaginationDto EMPTY = PaginationDto.builder().build();

    private Integer size;

    private Long offset;

    private Integer elementCount;
}
