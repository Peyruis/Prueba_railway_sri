package sri.project.sri_project.integration;


import org.springframework.stereotype.Component;

@Component
public class Esp32MqttRiego implements ControlRiego{

    private final Esp32MqttConnectionManager mqtt;


    public Esp32MqttRiego(Esp32MqttConnectionManager mqtt) {
        this.mqtt = mqtt;
    }

    @Override
    public void enviarComando(int comando) {

        if (mqtt.estaConectado()) {
            mqtt.publish(
                    "upt/riego/orden",
                    String.valueOf(comando)
            );
        }


    }
}
