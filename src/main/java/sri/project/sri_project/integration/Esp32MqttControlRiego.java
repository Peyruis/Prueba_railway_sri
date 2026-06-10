package sri.project.sri_project.integration;


import org.springframework.stereotype.Component;

@Component
public class Esp32MqttControlRiego implements ControlRiego{

    private final Esp32MqttConnectionManager mqtt;


    public Esp32MqttControlRiego(Esp32MqttConnectionManager mqtt) {
        this.mqtt = mqtt;
    }

    @Override
    public void enviarComando(int comando) {


        if (!mqtt.estaConectado()) {

            System.err.println("[MQTT] No conectado");

            return;
        }

        mqtt.publish("upt/riego/orden", String.valueOf(comando));

        System.out.println(
                "[MQTT] Comando enviado: "
                        + comando
        );




    }
}
