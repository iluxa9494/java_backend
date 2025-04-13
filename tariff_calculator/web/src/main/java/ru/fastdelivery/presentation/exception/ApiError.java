package ru.fastdelivery.presentation.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public record ApiError(
        @JsonIgnore
        HttpStatus httpStatus,
        String status,
        String message
) {
    public static ApiError badRequest(String message){
        return new ApiError(HttpStatus.BAD_REQUEST, "error", message);
    }
}
