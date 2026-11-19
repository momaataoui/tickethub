package ma.tickethub.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_tickets_category", columnList = "category_id"),
        @Index(name = "idx_tickets_buyer", columnList = "buyer_id")
})
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TicketCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User buyer;

    // Copie du prix au moment de l'achat : si l'organisateur change le prix
    // ensuite, le billet garde le prix réellement payé
    @Column(nullable = false)
    private BigDecimal pricePaid;

    @CreationTimestamp
    private Instant purchasedAt;
}
