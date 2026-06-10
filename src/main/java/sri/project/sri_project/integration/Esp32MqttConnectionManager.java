package sri.project.sri_project.integration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Esp32MqttConnectionManager {

    @Value("${mqtt.broker:ssl://d293d13a6a7c49bbbc434365cac41121.s1.eu.hivemq.cloud:8883}")
    private String broker;

    @Value("${mqtt.client-id:JavaAppClient}")
    private String clientId;

    @Value("${mqtt.username:saulupt}")
    private String username;

    @Value("${mqtt.password:Sistema_riego123}")
    private String password;

    @Getter
    private MqttClient client;

    @PostConstruct
    public void conectarAlIniciar() {
        try {
            conectar();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void conectar() {
        if (estaConectado()) {
            System.out.println("[MQTT] Cliente ya conectado");
            return;
        }

        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(broker, clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            System.out.println("[MQTT] Conectando...");
            client.connect(options);
            System.out.println("[MQTT] Conexión exitosa");
        } catch (MqttException ex) {
            throw new IllegalStateException("[MQTT] Error al conectar con el broker", ex);
        }
    }

    public void publish(String topic, String payload) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("El tópico MQTT es obligatorio.");
        }

        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("El payload MQTT es obligatorio.");
        }

        if (!estaConectado()) {
            throw new IllegalStateException("MQTT no está conectado.");
        }

        try {
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(1);
            client.publish(topic.trim(), message);
        } catch (MqttException e) {
            throw new IllegalStateException("[MQTT] Error publicando mensaje", e);
        }
    }

    public boolean estaConectado() {
        return client != null && client.isConnected();
    }
}
