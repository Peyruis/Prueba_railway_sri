package sri.project.sri_project.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sri.project.sri_project.model.enums.ModoRiego;
import sri.project.sri_project.repository.EventoRiegoRepository;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Controller
public class DashboardController {




    private final EventoRiegoRepository eventoRiegoRepository;


    @GetMapping("/dashboard")
    public String cargarDashboard(Model model) {

        // 1. Obtener los datos (Simulado por ahora, luego vendrán de tu base de datos)
        String nombreAgricultor = "Christian"; // Aquí sacarías el nombre del usuario logueado
        int humedadActual = 42; // estadisticasService.obtenerUltimaHumedad(sectorId);

        // 2. Generar la fecha y hora actual formateada
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaFormateada = ahora.format(formato);

        // 3. Preparar los datos históricos para el gráfico de Chart.js
        // Suponiendo que tu servicio devuelve una lista de enteros con las lecturas
        List<Integer> historialHumedad = Arrays.asList(
                40, 45, 52, 60, 67, 73, 66, 68, 62, 60,
                54, 52, 45, 42, 35, 32, 27, 29, 25, 26,
                24, 27, 26, 30, 32, 36, 34, 38, 40
        );

        // Convertimos la lista [40, 45, 52] a un String "40, 45, 52" para que JSP lo inserte en el JavaScript
        String valoresHumedadJSP = historialHumedad.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        // 4. Enviar los datos al modelo (JSP)
        model.addAttribute("usuarioNombre", nombreAgricultor);
        model.addAttribute("fechaActual", fechaFormateada);
        model.addAttribute("humedadActual", humedadActual);
        model.addAttribute("valoresHumedadJSP", valoresHumedadJSP);

        // 5. Retornar el nombre de la vista (sin la extensión .jsp)
        return "dashboard";
    }

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        long countManual = 0;
        long countAutomatico = 0;

        // 1. Envolver en un bloque try-catch para que si la BD falla, la página cargue igual con ceros
        try {
            List<Object[]> riegosPorModo = eventoRiegoRepository.contarRiegosPorModoMesActual();

            if (riegosPorModo != null) {
                for (Object[] fila : riegosPorModo) {
                    if (fila != null && fila[0] != null && fila[1] != null) {
                        ModoRiego modo = (ModoRiego) fila[0];
                        Long cantidad = (Long) fila[1];
                        if (modo == ModoRiego.MANUAL) countManual = cantidad;
                        if (modo == ModoRiego.AUTOMATICO) countAutomatico = cantidad;
                    }
                }
            }
        } catch (Exception e) {
            // Imprime el error real en la consola de IntelliJ para saber qué falló exactamente
            System.err.println("⚠️ Error al obtener datos de la BD: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Datos quemados seguros para el gráfico de barras
        String fechasJS = "['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom']";
        String duracionJS = "[1200, 1500, 900, 1800, 2000, 800, 1000]";

        // 3. Inyectar al modelo (asegurando que jamás vayan nulos)
        model.addAttribute("manuales", countManual);
        model.addAttribute("automaticos", countAutomatico);
        model.addAttribute("labelsDias", fechasJS);
        model.addAttribute("datosDuracion", duracionJS);

        return "estadisticas";
    }









}
