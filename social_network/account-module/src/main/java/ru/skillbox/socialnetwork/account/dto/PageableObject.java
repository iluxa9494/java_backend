package ru.skillbox.socialnetwork.account.dto;

import lombok.Data;

@Data
public class PageableObject {
    private Long offset;
    private SortObject sort;
    private Integer pageNumber;
    private Integer pageSize;
    private Boolean paged;
    private Boolean unpaged;
}
