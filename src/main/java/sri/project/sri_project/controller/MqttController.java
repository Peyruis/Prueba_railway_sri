package sri.project.sri_project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sri.project.sri_project.integration.Esp32MqttConnectionManager;

@Controller
@RequestMapping("/mqtt")
@RequiredArgsConstructor
public class MqttController {

    private final Esp32MqttConnectionManager mqttManager;

    @GetMapping
    public String vistaMqtt(Model model) {

        String estado = mqttManager.estaConectado()
                ? "CONECTADO"
                : "DESCONECTADO";

        model.addAttribute("estado", estado);

        return "mqtt";
    }

    @PostMapping("/connect")
    public String connect(RedirectAttributes redirectAttributes) {
        try {
            mqttManager.conectar();
            redirectAttributes.addFlashAttribute("mensaje", "Conexión MQTT establecida correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/mqtt";
    }

    @PostMapping("/publish")
    public String publish(
            @RequestParam String topic,
            @RequestParam String mensaje,
            RedirectAttributes redirectAttributes
    ) {
        try {
            mqttManager.publish(topic, mensaje);
            redirectAttributes.addFlashAttribute("mensaje", "Comando MQTT enviado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/mqtt";
    }

}
