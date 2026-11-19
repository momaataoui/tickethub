package ma.tickethub.backend.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_events_organizer", columnList = "organizer_id"),
        @Index(name = "idx_events_starts_at", columnList = "starts_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private Instant salesStartAt;

    // Décision humaine stockée en base (le statut de vente, lui, est calculé)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PUBLISHED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User organizer;

    // Composition : les catégories vivent et meurent avec l'événement
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("price DESC")
    @Setter(AccessLevel.NONE) // on passe par addCategory / removeCategory
    private List<TicketCategory> categories = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;

    // Garde les deux côtés de la relation synchronisés
    public void addCategory(TicketCategory category) {
        categories.add(category);
        category.setEvent(this);
    }

    public void removeCategory(TicketCategory category) {
        categories.remove(category);
        category.setEvent(null);
    }

    public int getTotalCapacity() {
        return categories.stream().mapToInt(TicketCategory::getCapacity).sum();
    }

    public int getAvailableSeats() {
        return categories.stream().mapToInt(TicketCategory::getAvailableSeats).sum();
    }

    /** Calculé à chaque appel ; "now" est passé en paramètre pour pouvoir tester. */
    public SalesStatus getSalesStatus(Instant now) {
        if (status == EventStatus.CANCELLED || !now.isBefore(startsAt)) {
            return SalesStatus.CLOSED;
        }
        if (now.isBefore(salesStartAt)) {
            return SalesStatus.NOT_OPEN_YET;
        }
        boolean allSoldOut = categories.stream()
                .allMatch(c -> c.getStatus() == CategoryStatus.SOLD_OUT);
        if (allSoldOut) {
            return SalesStatus.SOLD_OUT;
        }
        return SalesStatus.OPEN;
    }
}
