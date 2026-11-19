package ma.tickethub.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_categories",
        // Deux catégories d'un même événement ne peuvent pas avoir le même nom
        uniqueConstraints = @UniqueConstraint(name = "uk_ticket_categories_event_name", columnNames = { "event_id",
                "name" }), check = {
                        // Dernier filet de sécurité : PostgreSQL refuse toute survente, même si le code
                        // Java a un bug
                        @CheckConstraint(name = "chk_ticket_categories_sold_seats", constraint = "sold_seats >= 0 AND sold_seats <= capacity"),
                        @CheckConstraint(name = "chk_ticket_categories_capacity", constraint = "capacity > 0"),
                        @CheckConstraint(name = "chk_ticket_categories_price", constraint = "price >= 0")
                })
@Getter
@Setter
@NoArgsConstructor
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    @Column(nullable = false, length = 50)
    private String name; // VIP, GOLD, NORMAL...

    @Column(nullable = false)
    private BigDecimal price;

    private int capacity; // int primitif -> NOT NULL automatiquement

    private int soldSeats;

    // @Version sera ajouté à l'étape 5 (verrouillage optimiste)

    public int getAvailableSeats() {
        return capacity - soldSeats;
    }

    public CategoryStatus getStatus() {
        return soldSeats >= capacity ? CategoryStatus.SOLD_OUT : CategoryStatus.AVAILABLE;
    }
}
