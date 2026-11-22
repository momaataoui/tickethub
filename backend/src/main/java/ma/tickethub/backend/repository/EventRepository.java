package ma.tickethub.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ma.tickethub.backend.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Charge l'organisateur dans la même requête (LEFT JOIN) : plus de N requêtes "organizer"
    @Override
    @EntityGraph(attributePaths = "organizer")
    Page<Event> findAll(Pageable pageable);
}
