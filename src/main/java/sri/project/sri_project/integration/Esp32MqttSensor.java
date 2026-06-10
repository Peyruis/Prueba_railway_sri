package sri.project.sri_project.integration;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;
import sri.project.sri_project.dto.SensorData;
import sri.project.sri_project.service.serviceImpl.ProcesarDatosService;

import java.util.function.Consumer;

@Component
public class Esp32MqttSensor {

    private final Esp32MqttConnectionManager mqtt;
    private final ProcesarDatosService procesarDatosService;

    @Setter
    private Consumer<SensorData> onDataReceived;

    @Getter
    private SensorData ultimoDato;

    public Esp32MqttSensor(Esp32MqttConnectionManager mqtt, ProcesarDatosService procesarDatosService) {
        this.mqtt = mqtt;
        this.procesarDatosService = procesarDatosService;
    }


    @PostConstruct
    public void iniciar() {

        try {
            if (!mqtt.estaConectado() || mqtt.getClient() == null) {
                System.err.println("[MQTT] Sensor no suscrito: cliente desconectado");
                return;
            }

            MqttClient client = mqtt.getClient();

            client.subscribe("upt/riego/datos");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.err.println("[MQTT] Conexión perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {

                    String texto = new String(message.getPayload());
                    parsearLinea(texto);
                }

                @Override
                public void deliveryComplete(
                        IMqttDeliveryToken token
                ) {

                }
            });

            System.out.println("[MQTT] Suscrito a sensores");

        } catch (MqttException e) {

            System.err.println("[MQTT] Error subscriber");

            e.printStackTrace();
        }

    }

    private void parsearLinea(String linea) {

        try {

            if (!linea.contains(",")) {
                return;
            }

            String[] partes = linea.split(",");

            if (partes.length != 2) {
                System.err.println("Formato inválido");
                return;
            }

            int humedad = Integer.parseInt(partes[0].trim());

            double distancia = Double.parseDouble(partes[1].trim());

            SensorData sensorData = new SensorData(humedad, distancia);
            ultimoDato = sensorData;
            procesarDatosService.procesar(sensorData);

            if (onDataReceived != null) {
                onDataReceived.accept(sensorData);
            }

            System.out.println(
                    "[ESP32] H="
                            + humedad
                            + "% | D="
                            + distancia
                            + "cm"
            );

        } catch (Exception e) {

            System.err.println(
                    "[MQTT] Error parseando"
            );

            e.printStackTrace();
        }
    }


}
