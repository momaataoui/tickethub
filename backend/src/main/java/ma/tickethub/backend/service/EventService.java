package ma.tickethub.backend.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.dto.CategoryRequest;
import ma.tickethub.backend.dto.CreateEventRequest;
import ma.tickethub.backend.dto.EventResponse;
import ma.tickethub.backend.dto.UpdateEventRequest;
import ma.tickethub.backend.entity.Event;
import ma.tickethub.backend.entity.EventStatus;
import ma.tickethub.backend.entity.User;
import ma.tickethub.backend.exception.DuplicateCategoryNameException;
import ma.tickethub.backend.exception.EventCancelledException;
import ma.tickethub.backend.exception.EventNotFoundException;
import ma.tickethub.backend.exception.InvalidEventDatesException;
import ma.tickethub.backend.exception.NotEventOwnerException;
import ma.tickethub.backend.mapper.EventMapper;
import ma.tickethub.backend.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    // US1 : créer un événement avec ses catégories (tout ou rien)
    @Transactional
    public EventResponse create(CreateEventRequest request) {
        validateDates(request.salesStartAt(), request.startsAt());
        validateUniqueCategoryNames(request.categories());

        Event event = eventMapper.toEntity(request);
        event.setOrganizer(currentUserService.getCurrentUser());

        Event saved = eventRepository.save(event); // cascade : les catégories sont enregistrées aussi
        return eventMapper.toResponse(saved);
    }

    // US2 : liste paginée
    @Transactional(readOnly = true)
    public Page<EventResponse> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toResponse);
    }

    // US3 : détail d'un événement
    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return eventMapper.toResponse(event);
    }

    // US4 : modifier les informations d'un événement
    @Transactional
    public EventResponse update(Long id, UpdateEventRequest request) {
        Event event = findEditableEvent(id);
        validateDates(request.salesStartAt(), request.startsAt());

        eventMapper.updateEntity(event, request);
        // Pas de save() : l'entité est "managed", Hibernate fait l'UPDATE au commit (dirty checking)
        return eventMapper.toResponse(event);
    }

    // US6 : annuler un événement (suppression logique : la ligne reste en base)
    @Transactional
    public EventResponse cancel(Long id) {
        Event event = findEditableEvent(id);
        event.setStatus(EventStatus.CANCELLED); // -> getSalesStatus() renvoie CLOSED
        return eventMapper.toResponse(event);
    }

    // ---------- Règles de gestion ----------

    // Charge un événement que l'utilisateur connecté a le droit de modifier.
    // Public : CategoryService l'utilise aussi (la règle RG3 n'est écrite qu'une fois)
    public Event findEditableEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        // RG3 : seul l'organisateur de l'événement peut le modifier
        User currentUser = currentUserService.getCurrentUser();
        if (!event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new NotEventOwnerException(id);
        }
        // Un événement annulé est définitif
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new EventCancelledException(id);
        }
        return event;
    }

    private void validateDates(Instant salesStartAt, Instant startsAt) {
        if (!salesStartAt.isBefore(startsAt)) {
            throw new InvalidEventDatesException();
        }
    }

    // RG12 : "VIP" et "vip" comptent comme le même nom
    private void validateUniqueCategoryNames(List<CategoryRequest> categories) {
        Set<String> names = new HashSet<>();
        for (CategoryRequest category : categories) {
            String normalized = category.name().trim().toUpperCase();
            if (!names.add(normalized)) { // add() renvoie false si le nom est déjà dans le Set
                throw new DuplicateCategoryNameException(category.name());
            }
        }
    }
}
