package ma.tickethub.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import ma.tickethub.backend.entity.EventStatus;
import ma.tickethub.backend.entity.SalesStatus;

public record EventResponse(
        Long id,
        String title,
        String description,
        String venue,
        Instant startsAt,
        Instant salesStartAt,
        EventStatus status,
        SalesStatus salesStatus, // calculé
        BigDecimal minPrice, // pour "à partir de X DH"
        int totalCapacity,
        int availableSeats,
        String organizerName, // le nom seulement : jamais l'email ni le mot de passe
        List<CategoryResponse> categories) {
}
