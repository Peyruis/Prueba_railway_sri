/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package sri.project.sri_project.integration.Serial;


/**
 *
 * @author Usuario
 */


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import sri.project.sri_project.model.dto.SensorData;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;




@Component
public class Esp32SerialAdapter  {

    private SerialPort puerto;
    private Consumer<SensorData> onDataReceived;
    private StringBuilder bufferRecepcion = new StringBuilder();

    public void usarPuerto(SerialPort puerto) {
        this.puerto = puerto;
        iniciarLectura();
        System.out.println("[JAVA] Receptor configurado en puerto compartido");
    }
    public void setOnDataReceived(
            Consumer<SensorData> onDataReceived
    ) {
        this.onDataReceived = onDataReceived;
    }
    private void iniciarLectura() {

        puerto.addDataListener(new SerialPortDataListener() {

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {

                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;

                byte[] data = new byte[puerto.bytesAvailable()];
                puerto.readBytes(data, data.length);

                String texto = new String(data);
                procesarLineas(texto);
            }
        });
    }

    private void procesarLineas(String nuevosDatos) {
        bufferRecepcion.append(nuevosDatos);
        String contenido = bufferRecepcion.toString();

        while (contenido.contains("\n")) {
            int finLinea = contenido.indexOf("\n");
            String linea = contenido.substring(0, finLinea).trim();
            contenido = contenido.substring(finLinea + 1);

            if (!linea.isEmpty()) {
                parsearLinea(linea);
            }
        }

        bufferRecepcion = new StringBuilder(contenido);
    }

    private void parsearLinea(String linea) {
        try {

            if (!linea.contains(",")) return;

            // Este split:  "45,12"

            String[] partes = linea.split(",");

            if (partes.length != 2) {
                System.err.println(" Formato inválido: " + linea);
                return;
            }

            var humedad = Integer.parseInt(partes[0].trim());
            double distancia = Integer.parseInt(partes[1].trim());


            var sensorData = new SensorData(humedad,distancia);

            if (onDataReceived != null) {
                onDataReceived.accept(sensorData);
            }


            System.out.println("[ESP32] H=" + humedad + "% | D=" + distancia + "cm");

        } catch (NumberFormatException e) {
            System.err.println("Número inválido: " + linea);
        } catch (Exception e) {
            System.err.println("Parseando línea: " + linea);
        }
    }


}
