package sri.project.sri_project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sri.project.sri_project.integration.Esp32MqttSensor;
import sri.project.sri_project.dto.SensorData;

@Controller
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final Esp32MqttSensor sensor;

    @GetMapping
    public String vistaSensor(Model model) {

        SensorData data = sensor.getUltimoDato();

        if (data != null) {

            model.addAttribute(
                    "humedad",
                    data.humedad()
            );

            model.addAttribute(
                    "distancia",
                    data.distancia()
            );

        } else {

            model.addAttribute(
                    "humedad",
                    "Sin datos"
            );

            model.addAttribute(
                    "distancia",
                    "Sin datos"
            );
        }

        return "sensor";
    }
}