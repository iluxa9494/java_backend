package ru.skillbox.socialnetwork.dialog.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.List;

@Slf4j
public class SortUtils {

    private SortUtils() {}

    public static Sort parseSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by("id").ascending();
        }

        List<String> cleaned = sort.stream()
                .filter(s -> !s.equalsIgnoreCase("asc") && !s.equalsIgnoreCase("desc"))
                .toList();

        if (cleaned.isEmpty()) {
            return Sort.by("id").ascending();
        }

        if (cleaned.size() == 2 && !cleaned.get(0).contains(",") && !cleaned.get(1).contains(",")) {
            String property = cleaned.get(0);
            Sort.Direction direction = cleaned.get(1).equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return Sort.by(new Sort.Order(direction, property));
        }

        List<Sort.Order> orders = cleaned.stream().map(s -> {
            String[] parts = s.split(",");
            String property = parts[0];
            // the direction is switched to match the expectations of frontend
            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            return new Sort.Order(direction, property);
        }).toList();

        return Sort.by(orders);
    }
}
