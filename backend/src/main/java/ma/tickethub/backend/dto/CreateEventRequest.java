package ma.tickethub.backend.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(

        @NotBlank @Size(max = 150) String title,

        @Size(max = 2000) String description,

        @NotBlank String venue,

        @NotNull @Future Instant startsAt,

        @NotNull Instant salesStartAt,

        @NotEmpty // au moins une catégorie
        List<@Valid CategoryRequest> categories // @Valid : valide aussi chaque catégorie
) {
}
