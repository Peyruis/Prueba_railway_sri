package sri.project.sri_project.integration;

import com.fazecast.jSerialComm.SerialPort;

public interface Esp32ConnecionPort {

    boolean conectar(String puertoNombre);
    void escribirABytes(byte[] data);

    SerialPort getSerialPort();
    boolean estaConectado();
}
