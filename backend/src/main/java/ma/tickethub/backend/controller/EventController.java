package ma.tickethub.backend.controller;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.dto.CreateEventRequest;
import ma.tickethub.backend.dto.EventResponse;
import ma.tickethub.backend.dto.UpdateEventRequest;
import ma.tickethub.backend.service.EventService;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // POST /api/events -> 201 Created
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        EventResponse created = eventService.create(request);
        URI location = URI.create("/api/events/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // GET /api/events?page=0&size=10 -> 200 OK, trié par date
    @GetMapping
    public PagedModel<EventResponse> findAll(
            @PageableDefault(size = 10, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PagedModel<>(eventService.findAll(pageable));
    }

    // GET /api/events/5 -> 200 OK
    @GetMapping("/{id}")
    public EventResponse findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    // PUT /api/events/5 -> 200 OK
    @PutMapping("/{id}")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) {
        return eventService.update(id, request);
    }

    // POST /api/events/5/cancel -> 200 OK
    // POST et pas DELETE : l'événement n'est pas supprimé, il change d'état
    @PostMapping("/{id}/cancel")
    public EventResponse cancel(@PathVariable Long id) {
        return eventService.cancel(id);
    }
}