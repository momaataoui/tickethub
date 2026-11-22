package ma.tickethub.backend.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.tickethub.backend.dto.CategoryRequest;
import ma.tickethub.backend.dto.CategoryResponse;
import ma.tickethub.backend.service.CategoryService;

// Les catégories sont une sous-ressource de l'événement : /api/events/{eventId}/categories
@RestController
@RequestMapping("/api/events/{eventId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // POST /api/events/1/categories -> 201 Created
    @PostMapping
    public ResponseEntity<CategoryResponse> add(@PathVariable Long eventId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.add(eventId, request);
        URI location = URI.create("/api/events/" + eventId + "/categories/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // PUT /api/events/1/categories/2 -> 200 OK
    @PutMapping("/{categoryId}")
    public CategoryResponse update(@PathVariable Long eventId, @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(eventId, categoryId, request);
    }

    // DELETE /api/events/1/categories/2 -> 204 No Content (rien à renvoyer)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId, @PathVariable Long categoryId) {
        categoryService.delete(eventId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
