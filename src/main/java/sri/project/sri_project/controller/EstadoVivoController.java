package sri.project.sri_project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sri.project.sri_project.integration.Esp32MqttConnectionManager;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EstadoVivoController {

    private final Esp32MqttConnectionManager mqttConnectionManager;

    @GetMapping("/api/estado-vivo")
    public Map<String, Object> obtenerEstadoVivo() {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("mqtt_activo", mqttConnectionManager.estaConectado());
        estado.put("timestamp", LocalDateTime.now().toString());
        return estado;
    }

    @GetMapping("/api/mqtt/status")
    public Map<String, Boolean> obtenerEstadoMqtt() {
        return Map.of("conectado", mqttConnectionManager.estaConectado());
    }
}
