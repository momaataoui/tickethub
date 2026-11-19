package ma.tickethub.backend.entity;

/** Statut calculé de la billetterie (jamais stocké en base). */
public enum SalesStatus {
    NOT_OPEN_YET,
    OPEN,
    SOLD_OUT,
    CLOSED
}
