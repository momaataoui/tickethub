package ma.tickethub.backend.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.dto.CategoryRequest;
import ma.tickethub.backend.dto.CategoryResponse;
import ma.tickethub.backend.entity.Event;
import ma.tickethub.backend.entity.TicketCategory;
import ma.tickethub.backend.exception.CategoryHasTicketsException;
import ma.tickethub.backend.exception.CategoryNotFoundException;
import ma.tickethub.backend.exception.DuplicateCategoryNameException;
import ma.tickethub.backend.exception.InvalidCapacityException;
import ma.tickethub.backend.exception.LastCategoryException;
import ma.tickethub.backend.mapper.EventMapper;
import ma.tickethub.backend.repository.TicketCategoryRepository;

// US5 : l'organisateur gère les catégories de ses événements
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final EventService eventService; // réutilise RG3 (propriétaire, événement non annulé)
    private final TicketCategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public CategoryResponse add(Long eventId, CategoryRequest request) {
        Event event = eventService.findEditableEvent(eventId);
        checkNameIsFree(event, request.name(), null); // RG12

        TicketCategory category = eventMapper.toEntity(request);
        event.addCategory(category);
        categoryRepository.save(category); // save() pour obtenir l'id dans la réponse
        return eventMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse update(Long eventId, Long categoryId, CategoryRequest request) {
        Event event = eventService.findEditableEvent(eventId);
        TicketCategory category = findCategory(event, categoryId);

        checkNameIsFree(event, request.name(), categoryId); // RG12 (sans se compter elle-même)
        if (request.capacity() < category.getSoldSeats()) { // RG8
            throw new InvalidCapacityException(request.capacity(), category.getSoldSeats());
        }

        eventMapper.updateEntity(category, request);
        return eventMapper.toResponse(category); // dirty checking : UPDATE au commit
    }

    @Transactional
    public void delete(Long eventId, Long categoryId) {
        Event event = eventService.findEditableEvent(eventId);
        TicketCategory category = findCategory(event, categoryId);

        if (category.getSoldSeats() > 0) { // RG14
            throw new CategoryHasTicketsException(category.getName());
        }
        if (event.getCategories().size() == 1) { // RG11
            throw new LastCategoryException();
        }
        event.removeCategory(category); // orphanRemoval = true -> DELETE au commit
    }

    // ---------- Règles de gestion ----------

    // La catégorie doit appartenir à CET événement (sinon /events/1/categories/7 modifierait l'événement 2)
    private TicketCategory findCategory(Event event, Long categoryId) {
        return event.getCategories().stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    // RG12 : "VIP" et "vip" comptent comme le même nom
    private void checkNameIsFree(Event event, String name, Long excludedCategoryId) {
        boolean taken = event.getCategories().stream()
                .filter(c -> !Objects.equals(c.getId(), excludedCategoryId))
                .anyMatch(c -> c.getName().trim().equalsIgnoreCase(name.trim()));
        if (taken) {
            throw new DuplicateCategoryNameException(name);
        }
    }
}
