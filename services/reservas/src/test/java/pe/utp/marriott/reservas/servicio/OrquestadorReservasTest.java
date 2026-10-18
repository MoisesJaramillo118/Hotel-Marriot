package pe.utp.marriott.reservas.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import pe.utp.marriott.reservas.api.CrearReservaRequest;
import pe.utp.marriott.reservas.comun.Secuencias;
import pe.utp.marriott.reservas.error.ApiException;
import pe.utp.marriott.reservas.integracion.ClientesClient;
import pe.utp.marriott.reservas.integracion.ErrorRemoto;
import pe.utp.marriott.reservas.integracion.HabitacionesClient;
import pe.utp.marriott.reservas.integracion.NotificacionesClient;
import pe.utp.marriott.reservas.integracion.PagosClient;
import pe.utp.marriott.reservas.integracion.PaquetesClient;
import pe.utp.marriott.reservas.integracion.Remotos;
import pe.utp.marriott.reservas.integracion.ToursClient;
import pe.utp.marriott.reservas.modelo.EstadoReserva;
import pe.utp.marriott.reservas.modelo.Reserva;
import pe.utp.marriott.reservas.modelo.ReservaRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrquestadorReservasTest {

    private static final LocalDate DESDE = LocalDate.now().plusDays(15);
    private static final LocalDate HASTA = DESDE.plusDays(3);

    @Mock ReservaRepository reservas;
    @Mock Secuencias secuencias;
    @Mock ClientesClient clientes;
    @Mock HabitacionesClient habitaciones;
    @Mock ToursClient tours;
    @Mock PaquetesClient paquetes;
    @Mock PagosClient pagos;
    @Mock NotificacionesClient notificaciones;

    OrquestadorReservas orquestador;

    @BeforeEach
    void preparar() {
        orquestador = new OrquestadorReservas(reservas, secuencias, clientes, habitaciones, tours, paquetes, pagos,
                notificaciones);
        when(secuencias.siguiente(anyString(), anyString(), any(Integer.class))).thenReturn("RES-004521");
        when(reservas.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clientes.obtener("CLI-000123")).thenReturn(
                new Remotos.Cliente("CLI-000123", "Mario Iván", "Aponte Matos", "mario@example.com", "ACTIVO"));
        when(habitaciones.obtener("HAB-301")).thenReturn(
                new Remotos.Habitacion("HAB-301", "SUITE", new BigDecimal("920.00"), "PEN", "DISPONIBLE"));
        when(tours.obtener("TOU-012")).thenReturn(new Remotos.Tour("TOU-012", "City tour Lima Colonial",
                DESDE.plusDays(1), 20, new BigDecimal("380.00"), "PEN"));
    }

    private static CrearReservaRequest solicitud(String tourId, String token) {
        return new CrearReservaRequest("CLI-000123", "HAB-301", DESDE, HASTA, tourId, null,
                new CrearReservaRequest.DatosPago("TARJETA", token));
    }

    @Test
    void reservaIntegradaQuedaConfirmadaConElTotalDelContrato() {
        when(pagos.cobrar(any(), eq("PEN"), eq("TARJETA"), eq("tok_abc123"), eq("RES-004521")))
                .thenReturn(new Remotos.Pago("PAG-000001", "APROBADO", "txn_99812"));

        Reserva r = orquestador.crear(solicitud("TOU-012", "tok_abc123"));

        assertThat(r.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(r.getTotal()).isEqualByComparingTo("3140.00");
        assertThat(r.getListaItems()).containsExactly("HAB-301 x3 noches", "TOU-012");
        assertThat(r.getPagoTxnId()).isEqualTo("txn_99812");
        verify(habitaciones).bloquear("HAB-301", "RES-004521", DESDE, HASTA);
        verify(tours).reservar("TOU-012", "CLI-000123", "RES-004521");
        ArgumentCaptor<BigDecimal> monto = ArgumentCaptor.forClass(BigDecimal.class);
        verify(pagos).cobrar(monto.capture(), anyString(), anyString(), anyString(), anyString());
        assertThat(monto.getValue()).isEqualByComparingTo("3140.00");
        verify(notificaciones).avisar(eq("mario@example.com"), eq("RESERVA_CONFIRMADA"), any());
    }

    @Test
    void sinCupoDevuelve409ConSugerenciasYNoCobra() {
        doThrow(new ErrorRemoto("Habitaciones", 409, Map.of("error", "SIN_DISPONIBILIDAD",
                "mensaje", "La habitación HAB-301 no tiene cupo en el rango solicitado.",
                "sugerencias", List.of("HAB-305", "HAB-410"))))
                .when(habitaciones).bloquear(anyString(), anyString(), any(), any());

        assertThatThrownBy(() -> orquestador.crear(solicitud("TOU-012", "tok_abc123")))
                .isInstanceOfSatisfying(ApiException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(e.getCodigo()).isEqualTo("SIN_DISPONIBILIDAD");
                    assertThat(e.getExtra()).containsEntry("sugerencias", List.of("HAB-305", "HAB-410"));
                });

        verify(tours, never()).reservar(anyString(), anyString(), anyString());
        verify(pagos, never()).cobrar(any(), anyString(), anyString(), anyString(), anyString());
        verify(habitaciones, never()).liberar(anyString());
        assertThat(ultimaGuardada().getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void pagoRechazadoDevuelve402YDeshaceHabitacionYTour() {
        when(pagos.cobrar(any(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new ErrorRemoto("Pagos", 402, Map.of("error", "PAGO_RECHAZADO",
                        "mensaje", "La entidad emisora rechazó la tarjeta.")));

        assertThatThrownBy(() -> orquestador.crear(solicitud("TOU-012", "tok_rechazo")))
                .isInstanceOfSatisfying(ApiException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
                    assertThat(e.getCodigo()).isEqualTo("PAGO_RECHAZADO");
                });

        verify(tours).cancelar("RES-004521");
        verify(habitaciones).liberar("RES-004521");
        verify(pagos, never()).reembolsar(anyString());
        verify(notificaciones, never()).avisar(anyString(), anyString(), any());
        assertThat(ultimaGuardada().getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void tourSinCupoLiberaLaHabitacion() {
        doThrow(new ErrorRemoto("Tours", 409, Map.of("error", "SIN_CUPO", "mensaje", "Sin cupo.")))
                .when(tours).reservar(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> orquestador.crear(solicitud("TOU-012", "tok_abc123")))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT));

        verify(habitaciones).liberar("RES-004521");
        verify(pagos, never()).cobrar(any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void clienteInexistenteEsUn400SinTocarNada() {
        when(clientes.obtener("CLI-999999")).thenThrow(new ErrorRemoto("Clientes", 404,
                Map.of("error", "NO_ENCONTRADO", "mensaje", "El cliente CLI-999999 no existe.")));
        CrearReservaRequest req = new CrearReservaRequest("CLI-999999", "HAB-301", DESDE, HASTA, null, null,
                new CrearReservaRequest.DatosPago("TARJETA", "tok_abc123"));

        assertThatThrownBy(() -> orquestador.crear(req))
                .isInstanceOfSatisfying(ApiException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getCodigo()).isEqualTo("CLIENTE_NO_ENCONTRADO");
                });
        verify(reservas, never()).save(any());
    }

    @Test
    void tourFueraDeLaEstadiaEsUn400() {
        when(tours.obtener("TOU-099")).thenReturn(new Remotos.Tour("TOU-099", "Lejano", HASTA.plusDays(10), 5,
                new BigDecimal("100.00"), "PEN"));

        assertThatThrownBy(() -> orquestador.crear(solicitud("TOU-099", "tok_abc123")))
                .isInstanceOfSatisfying(ApiException.class,
                        e -> assertThat(e.getCodigo()).isEqualTo("TOUR_FUERA_DE_ESTADIA"));
    }

    @Test
    void cancelarLiberaDevuelveCupoYReembolsa() {
        Reserva r = new Reserva();
        r.setId("RES-004521");
        r.setClienteId("CLI-000123");
        r.setHabitacionId("HAB-301");
        r.setTourId("TOU-012");
        r.setDesde(DESDE);
        r.setHasta(HASTA);
        r.setTotal(new BigDecimal("3140.00"));
        r.setMoneda("PEN");
        r.setEstado(EstadoReserva.CONFIRMADA);
        r.setPagoTxnId("txn_99812");
        r.setPagoEstado("APROBADO");
        when(reservas.findById("RES-004521")).thenReturn(Optional.of(r));
        when(pagos.reembolsar("txn_99812")).thenReturn(new Remotos.Pago("PAG-000001", "REEMBOLSADO", "txn_99812"));

        Reserva cancelada = orquestador.cancelar("RES-004521");

        assertThat(cancelada.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(cancelada.getPagoEstado()).isEqualTo("REEMBOLSADO");
        verify(habitaciones).liberar("RES-004521");
        verify(tours).cancelar("RES-004521");
        verify(notificaciones).avisar(eq("mario@example.com"), eq("RESERVA_CANCELADA"), any());
    }

    @Test
    void noSeCancelaDosVeces() {
        Reserva r = new Reserva();
        r.setId("RES-004521");
        r.setEstado(EstadoReserva.CANCELADA);
        when(reservas.findById("RES-004521")).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> orquestador.cancelar("RES-004521"))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getCodigo()).isEqualTo("YA_CANCELADA"));
    }

    private Reserva ultimaGuardada() {
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservas, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }
}
