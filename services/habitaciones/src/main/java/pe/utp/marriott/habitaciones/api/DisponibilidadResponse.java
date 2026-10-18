package pe.utp.marriott.habitaciones.api;

import java.util.List;

public record DisponibilidadResponse(List<HabitacionResponse> disponibles, int total) {
}
