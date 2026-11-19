package ma.tickethub.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.tickethub.backend.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
