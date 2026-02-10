package ru.skillbox.socialnetwork.account.dto;

import java.util.List;

public record PageAccountDto(
        Integer totalElements,    //   Long на Integer
        Integer totalPages,
        Integer size,
        List<AccountDto> content,
        Integer number,
        SortObject sort,
        Boolean first,
        Boolean last,
        Integer numberOfElements,
        PageableObject pageable,
        Boolean empty
) {}