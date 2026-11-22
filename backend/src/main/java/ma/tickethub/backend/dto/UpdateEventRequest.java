package ma.tickethub.backend.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Pas de catégories ici : elles ont leurs propres endpoints (RG8, RG14)
public record UpdateEventRequest(

        @NotBlank @Size(max = 150) String title,

        @Size(max = 2000) String description,

        @NotBlank String venue,

        @NotNull @Future Instant startsAt,

        @NotNull Instant salesStartAt) {
}
