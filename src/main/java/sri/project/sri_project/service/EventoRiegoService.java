package sri.project.sri_project.service;

import sri.project.sri_project.dto.SensorData;
import sri.project.sri_project.model.Cultivo;
import sri.project.sri_project.model.enums.ModoRiego;

public interface EventoRiegoService {

    void registrarInicio(Cultivo cultivo, ModoRiego modoRiego, SensorData lecturaInicial);

    void completarRiego(SensorData lecturaFinal);
}
