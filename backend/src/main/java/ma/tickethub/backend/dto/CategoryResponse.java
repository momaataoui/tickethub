package ma.tickethub.backend.dto;

import java.math.BigDecimal;

import ma.tickethub.backend.entity.CategoryStatus;

public record CategoryResponse(
        Long id,
        String name,
        BigDecimal price,
        int capacity,
        int availableSeats,
        CategoryStatus status) {
}
