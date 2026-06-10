package sri.project.sri_project.service.serviceImpl;

import org.springframework.stereotype.Service;
import sri.project.sri_project.model.ConfiguracionRiego;
import sri.project.sri_project.dto.SensorData;
import sri.project.sri_project.model.LecturaSensor;
import sri.project.sri_project.model.Policy.SeguridadHidrica;
import sri.project.sri_project.model.TanqueAgua;
import sri.project.sri_project.repository.ConfiguracionRiegoRepository;
import sri.project.sri_project.repository.LecturaSensorRepository;

import java.util.Optional;

@Service
public class ProcesarDatosService {

    private final TanqueAgua tanque;
    private final SeguridadHidrica seguridadHidrica;
    private final LecturaSensorRepository lecturaSensorRepository;
    private final ConfiguracionRiegoRepository configuracionRiegoRepository;

    public ProcesarDatosService(
            TanqueAgua tanque,
            SeguridadHidrica seguridadHidrica,
            LecturaSensorRepository lecturaSensorRepository,
            ConfiguracionRiegoRepository configuracionRiegoRepository
    ) {
        this.tanque = tanque;
        this.seguridadHidrica = seguridadHidrica;
        this.lecturaSensorRepository = lecturaSensorRepository;
        this.configuracionRiegoRepository = configuracionRiegoRepository;
    }

    public void procesar(SensorData data) {
        guardarLectura(data);

        tanque.actualizarDatos(
                data.humedad(),
                data.distancia()
        );

        seguridadHidrica.evaluarEstado(tanque);
    }

    private void guardarLectura(SensorData data) {
        LecturaSensor lectura = new LecturaSensor();
        lectura.setHumedadSuelo(data.humedad());
        lectura.setDistanciaAgua(data.distancia());
        lectura.setCultivoId(obtenerCultivoActivoId().orElse(null));

        lecturaSensorRepository.save(lectura);
    }

    private Optional<Integer> obtenerCultivoActivoId() {
        return configuracionRiegoRepository.findById(1)
                .map(ConfiguracionRiego::getCultivoActivo)
                .map(cultivo -> cultivo.getId());
    }
}
