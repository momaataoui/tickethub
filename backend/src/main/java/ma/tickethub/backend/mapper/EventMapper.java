package ma.tickethub.backend.mapper;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.dto.CategoryRequest;
import ma.tickethub.backend.dto.CategoryResponse;
import ma.tickethub.backend.dto.CreateEventRequest;
import ma.tickethub.backend.dto.EventResponse;
import ma.tickethub.backend.entity.Event;
import ma.tickethub.backend.entity.TicketCategory;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final Clock clock;

    // ---------- DTO -> Entité (création) ----------

    public Event toEntity(CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartsAt(request.startsAt());
        event.setSalesStartAt(request.salesStartAt());
        // L'organisateur n'est PAS dans le JSON : c'est le service qui l'ajoute
        for (CategoryRequest categoryRequest : request.categories()) {
            event.addCategory(toEntity(categoryRequest)); // addCategory lie les deux côtés
        }
        return event;
    }

    public TicketCategory toEntity(CategoryRequest request) {
        TicketCategory category = new TicketCategory();
        category.setName(request.name());
        category.setPrice(request.price());
        category.setCapacity(request.capacity());
        category.setSoldSeats(0); // une nouvelle catégorie n'a rien vendu
        return category;
    }

    // ---------- Entité -> DTO (affichage) ----------

    public EventResponse toResponse(Event event) {
        Instant now = Instant.now(clock);
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getStartsAt(),
                event.getSalesStartAt(),
                event.getStatus(),
                event.getSalesStatus(now),
                minPrice(event),
                event.getTotalCapacity(),
                event.getAvailableSeats(),
                event.getOrganizer().getFullName(),
                event.getCategories().stream().map(this::toResponse).toList());
    }

    public CategoryResponse toResponse(TicketCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getPrice(),
                category.getCapacity(),
                category.getAvailableSeats(),
                category.getStatus());
    }

    // Prix le plus bas, pour afficher "à partir de X DH"
    private BigDecimal minPrice(Event event) {
        return event.getCategories().stream()
                .map(TicketCategory::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }
}
