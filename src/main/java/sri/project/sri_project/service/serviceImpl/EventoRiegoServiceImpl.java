package sri.project.sri_project.service.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sri.project.sri_project.dto.SensorData;
import sri.project.sri_project.model.Cultivo;
import sri.project.sri_project.model.EventoRiego;
import sri.project.sri_project.model.enums.EstadoRiego;
import sri.project.sri_project.model.enums.ModoRiego;
import sri.project.sri_project.repository.EventoRiegoRepository;
import sri.project.sri_project.service.EventoRiegoService;

import java.time.LocalDateTime;

@Service
public class EventoRiegoServiceImpl implements EventoRiegoService {

    private final EventoRiegoRepository eventoRiegoRepository;

    public EventoRiegoServiceImpl(EventoRiegoRepository eventoRiegoRepository) {
        this.eventoRiegoRepository = eventoRiegoRepository;
    }

    @Override
    @Transactional
    public void registrarInicio(Cultivo cultivo, ModoRiego modoRiego, SensorData lecturaInicial) {
        if (modoRiego == null || lecturaInicial == null) {
            System.err.println("[EVENTO-RIEGO] Inicio no registrado: faltan modo o lectura inicial.");
            return;
        }

        if (eventoRiegoRepository.findFirstByEstadoOrderByFechaInicioDesc(EstadoRiego.EN_PROCESO).isPresent()) {
            System.out.println("[EVENTO-RIEGO] Ya existe un riego en proceso. No se crea otro evento.");
            return;
        }

        EventoRiego evento = new EventoRiego();
        evento.setCultivo(cultivo);
        evento.setModoRiego(modoRiego);
        evento.setFechaInicio(LocalDateTime.now());
        evento.setHumedadSueloInicial(lecturaInicial.humedad());
        evento.setEstado(EstadoRiego.EN_PROCESO);

        eventoRiegoRepository.save(evento);
        String nombreCultivo = cultivo != null ? cultivo.getNombre() : "Riego de Mantenimiento";
        System.out.println("[EVENTO-RIEGO] Inicio registrado para " + nombreCultivo + " en modo " + modoRiego + ".");
    }

    @Override
    @Transactional
    public void completarRiego(SensorData lecturaFinal) {
        eventoRiegoRepository.findFirstByEstadoOrderByFechaInicioDesc(EstadoRiego.EN_PROCESO)
                .ifPresentOrElse(evento -> completarEvento(evento, lecturaFinal), () ->
                        System.out.println("[EVENTO-RIEGO] No hay riego en proceso para completar."));
    }

    private void completarEvento(EventoRiego evento, SensorData lecturaFinal) {
        evento.setFechaFin(LocalDateTime.now());
        evento.setHumedadSueloFinal(lecturaFinal != null ? lecturaFinal.humedad() : evento.getHumedadSueloInicial());
        evento.setEstado(EstadoRiego.COMPLETADO);

        eventoRiegoRepository.save(evento);
        System.out.println("[EVENTO-RIEGO] Riego completado. Evento ID: " + evento.getId());
    }
}
