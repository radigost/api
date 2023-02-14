package com.socialnetwork.api.v1.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedListResponseDto<P> {
    // TODO add type to the list
    private List<?> list;

    private PaginationDto pagination;


}
