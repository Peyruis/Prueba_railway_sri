package sri.project.sri_project.service;

import sri.project.sri_project.dto.RiegoEstadoResponse;
import sri.project.sri_project.model.Cultivo;
import sri.project.sri_project.model.enums.ModoOperacion;

import java.time.LocalTime;
import java.util.Optional;

public interface RiegoControlService {

    RiegoEstadoResponse obtenerEstado();

    RiegoEstadoResponse cambiarModo(ModoOperacion modoOperacion);

    RiegoEstadoResponse seleccionarPerfilAutomatico(Integer cultivoId);

    RiegoEstadoResponse programarRiegoAutomatico(Integer cultivoId, String horaRiego);

    RiegoEstadoResponse ejecutarOrdenManual(String orden);

    ModoOperacion obtenerModoActual();

    Optional<Cultivo> obtenerCultivoAutomaticoSeleccionado();

    Optional<LocalTime> obtenerHoraRiegoProgramada();
}
