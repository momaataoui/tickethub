package ma.tickethub.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.tickethub.backend.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
