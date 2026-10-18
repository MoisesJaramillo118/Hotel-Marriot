package pe.utp.marriott.reservas.modelo;

/** Ciclo de la reserva: PENDIENTE → CONFIRMADA → CANCELADA (o PENDIENTE → CANCELADA si el flujo falla). */
public enum EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA
}
