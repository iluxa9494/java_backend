package com.example.hotel_booking.dto;

import com.example.hotel_booking.model.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;

    @NotNull
    private RoleType name;
}
