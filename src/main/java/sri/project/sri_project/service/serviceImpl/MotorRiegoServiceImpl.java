package sri.project.sri_project.service.serviceImpl;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sri.project.sri_project.dto.SensorData;
import sri.project.sri_project.integration.ControlRiego;
import sri.project.sri_project.integration.Esp32MqttSensor;
import sri.project.sri_project.model.Cultivo;
import sri.project.sri_project.model.enums.ModoOperacion;
import sri.project.sri_project.model.enums.ModoRiego;
import sri.project.sri_project.service.EventoRiegoService;
import sri.project.sri_project.service.MotorRiegoService;
import sri.project.sri_project.service.RiegoControlService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Service
public class MotorRiegoServiceImpl implements MotorRiegoService {

    private static final int COMANDO_OFF = 0;
    private static final int COMANDO_ON = 1;

    private final RiegoControlService riegoControlService;
    private final Esp32MqttSensor esp32MqttSensor;
    private final ControlRiego controlRiego;
    private final TaskScheduler taskScheduler;
    private final EventoRiegoService eventoRiegoService;

    private boolean riegoAutomaticoActivo = false;
    private LocalDate ultimaEjecucionProgramada;
    private ScheduledFuture<?> apagadoProgramado;

    public MotorRiegoServiceImpl(
            RiegoControlService riegoControlService,
            Esp32MqttSensor esp32MqttSensor,
            ControlRiego controlRiego,
            TaskScheduler taskScheduler,
            EventoRiegoService eventoRiegoService
    ) {
        this.riegoControlService = riegoControlService;
        this.esp32MqttSensor = esp32MqttSensor;
        this.controlRiego = controlRiego;
        this.taskScheduler = taskScheduler;
        this.eventoRiegoService = eventoRiegoService;
    }

    @Override
    @Scheduled(cron = "0 * * * * *")
    public synchronized void evaluarRiegoAutomatico() {
        if (riegoControlService.obtenerModoActual() != ModoOperacion.AUTOMATICO) {
            return;
        }

        if (riegoAutomaticoActivo) {
            return;
        }

        Optional<Cultivo> cultivoActivo = riegoControlService.obtenerCultivoAutomaticoSeleccionado();
        Optional<LocalTime> horaProgramada = riegoControlService.obtenerHoraRiegoProgramada();
        SensorData ultimoDato = esp32MqttSensor.getUltimoDato();

        if (cultivoActivo.isEmpty() || horaProgramada.isEmpty() || ultimoDato == null) {
            return;
        }

        if (!esHoraProgramada(horaProgramada.get())) {
            return;
        }

        Cultivo cultivo = cultivoActivo.get();
        if (ultimoDato.humedad() >= cultivo.getHumedadMinOptima()) {
            ultimaEjecucionProgramada = LocalDate.now();
            System.out.println("[AUTO-RIEGO] Humedad suficiente. Riego omitido para " + cultivo.getNombre() + ".");
            return;
        }

        iniciarRiegoProgramado(cultivo);
    }

    private boolean esHoraProgramada(LocalTime horaProgramada) {
        LocalTime ahora = LocalTime.now();
        LocalDate hoy = LocalDate.now();

        return ahora.getHour() == horaProgramada.getHour()
                && ahora.getMinute() == horaProgramada.getMinute()
                && !hoy.equals(ultimaEjecucionProgramada);
    }

    private void iniciarRiegoProgramado(Cultivo cultivo) {
        Integer duracionMinutos = cultivo.getDuracionRiegoMinutos();

        if (duracionMinutos == null || duracionMinutos <= 0) {
            ultimaEjecucionProgramada = LocalDate.now();
            System.err.println("[AUTO-RIEGO] Perfil sin duracion valida. Riego omitido para " + cultivo.getNombre() + ".");
            return;
        }

        controlRiego.enviarComando(COMANDO_ON);
        eventoRiegoService.registrarInicio(cultivo, ModoRiego.AUTOMATICO, esp32MqttSensor.getUltimoDato());
        riegoAutomaticoActivo = true;
        ultimaEjecucionProgramada = LocalDate.now();

        Instant apagadoEn = Instant.now().plusSeconds(duracionMinutos * 60L);
        apagadoProgramado = taskScheduler.schedule(this::apagarRiegoProgramado, apagadoEn);

        System.out.println(
                "[AUTO-RIEGO] Bomba encendida para "
                        + cultivo.getNombre()
                        + " durante "
                        + duracionMinutos
                        + " minuto(s)."
        );
    }

    private synchronized void apagarRiegoProgramado() {
        if (!riegoAutomaticoActivo) {
            return;
        }

        controlRiego.enviarComando(COMANDO_OFF);
        eventoRiegoService.completarRiego(esp32MqttSensor.getUltimoDato());
        riegoAutomaticoActivo = false;
        apagadoProgramado = null;

        System.out.println("[AUTO-RIEGO] Duracion configurada cumplida. Bomba apagada automaticamente.");
    }
}
