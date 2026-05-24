package sri.project.sri_project.integration.Serial;


import com.fazecast.jSerialComm.SerialPort;
import org.springframework.context.annotation.Configuration;
import sri.project.sri_project.integration.Esp32ConnecionPort;


@Configuration
public class Esp32ConnectionManager implements Esp32ConnecionPort {

    private SerialPort puerto;

    public boolean conectar(String puertoNombre) {
        puerto = SerialPort.getCommPort(puertoNombre);
        puerto.setBaudRate(115200);
        return puerto.openPort();
    }

    public boolean estaConectado() {
        return puerto != null && puerto.isOpen();
    }


    public SerialPort getSerialPort() {
        return puerto;
    }

    // reemplaza la participacion del writeBytes en el Adapter con esta funciion void
    public void escribirABytes(byte[] data) {
        puerto.writeBytes(data, data.length);
    }


}
