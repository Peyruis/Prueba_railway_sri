package sri.project.sri_project.service.serviceImpl;

import sri.project.sri_project.service.ConectarEsp32;
import sri.project.sri_project.integration.Esp32ConnecionPort;
import sri.project.sri_project.integration.Serial.Esp32SerialAdapter;
import org.springframework.stereotype.Service;


@Service
public class ConectarESP32ServiceImpl implements ConectarEsp32 {

    private final Esp32ConnecionPort connectionManager;
    private final Esp32SerialAdapter serialAdapter;
    private final ProcesarDatosService procesarDatosInteractor;


    public ConectarESP32ServiceImpl(Esp32ConnecionPort connectionManager, Esp32SerialAdapter serialAdapter, ProcesarDatosService procesarDatosInteractor) {
        this.connectionManager = connectionManager;
        this.serialAdapter = serialAdapter;
        this.procesarDatosInteractor = procesarDatosInteractor;
    }


    @Override
    public boolean ejecutar(String puerto) {

        boolean conectado = connectionManager.conectar(puerto);

        if (conectado){

            serialAdapter.setOnDataReceived(
                    procesarDatosInteractor::procesar
            );

            serialAdapter.usarPuerto(connectionManager.getSerialPort());
        }

        return conectado;
    }

}
