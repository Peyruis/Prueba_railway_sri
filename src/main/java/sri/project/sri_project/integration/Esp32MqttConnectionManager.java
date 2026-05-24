package sri.project.sri_project.integration;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Component;

@Component
public class Esp32MqttConnectionManager {


    private String broker = "ssl://d293d13a6a7c49bbbc434365cac41121.s1.eu.hivemq.cloud:8883";
    private String clientId = "JavaAppClient";
    private MqttClient client;



    public void publish(String topic, String payload) {

    }

    public void subscribe(String topic) {

    }

    public boolean estaConectado() {
        return client != null && client.isConnected();
    }

}
