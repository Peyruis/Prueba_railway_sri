package sri.project.sri_project.integration;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Esp32HttpConnectionManager implements ControlRiego {

    private String broker = "ssl://d293d13a6a7c49bbbc434365cac41121.s1.eu.hivemq.cloud:8883"; // Usa 1883 para pruebas iniciales sin SSL
    private String clientId = "JavaAppClient";
    private MqttClient client;

    public Esp32HttpConnectionManager() {
        try {
            // Usamos MemoryPersistence para evitar errores de escritura en disco
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(broker, clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("saulupt"); // El que hiciste en Access Management
            options.setPassword("Sistema_riego123".toCharArray());

            // Configuraciones de estabilidad
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(60);

            System.out.println("Conectando al broker: " + broker);
            client.connect(options);
            System.out.println("¡CONEXIÓN EXITOSA CON HIVEMQ!");

        } catch (MqttException e) {
            System.err.println("ERROR DE CONEXIÓN: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void enviarComando(int comando) {
        try {
            if (client != null && client.isConnected()) {
                String mensajeTexto = String.valueOf(comando);
                MqttMessage message = new MqttMessage(mensajeTexto.getBytes());
                message.setQos(1); // Asegura que el mensaje llegue

                client.publish("upt/riego/orden", message);
                System.out.println("Orden enviada a la nube: " + comando);
            } else {
                System.out.println("No se pudo enviar: Cliente no conectado.");
            }
        } catch (MqttException e) {
            System.err.println("Error al publicar: " + e.getMessage());
        }
    }

    // Método extra para que tu UI sepa de qué color ponerse
    public boolean estaConectado() {
        return client != null && client.isConnected();
    }

}