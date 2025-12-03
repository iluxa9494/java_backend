package com.example.hotel_booking.mapper;

import com.example.hotel_booking.dto.Statistics.StatisticsDto;
import com.example.hotel_booking.model.Statistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StatisticsMapper {
    StatisticsMapper INSTANCE = Mappers.getMapper(StatisticsMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "createdAt", source = "createdAt")
    StatisticsDto toDto(Statistics statistics);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "createdAt", source = "createdAt")
    Statistics toEntity(StatisticsDto statisticsDto);
}
