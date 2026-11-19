package ma.tickethub.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.tickethub.backend.entity.TicketCategory;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
}
