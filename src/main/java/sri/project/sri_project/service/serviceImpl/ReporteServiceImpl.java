package sri.project.sri_project.service.serviceImpl;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import sri.project.sri_project.model.Cultivo;
import sri.project.sri_project.repository.PerfilCultivoRepository;
import sri.project.sri_project.service.ReporteService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class ReporteServiceImpl implements ReporteService {

    private static final String CULTIVO_MANTENIMIENTO = "mantenimiento";
    private static final String NOMBRE_MANTENIMIENTO = "Mantenimiento / Sin Cultivo";

    private final PerfilCultivoRepository perfilCultivoRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] generarReporteModosRiegoPDF(LocalDate fechaInicio, LocalDate fechaFin, String cultivoId)
            throws JRException, FileNotFoundException {

        FiltroReporte filtro = resolverFiltro(cultivoId);
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.plusDays(1).atStartOfDay() : null;

        Collection<Map<String, ?>> datosReporte = new ArrayList<>();
        for (Map<String, Object> fila : obtenerFilasReporte(inicio, fin, filtro)) {
            datosReporte.add(toReporteRow(fila));
        }

        ClassPathResource plantilla = new ClassPathResource("reportes/grafico_modos_riego.jrxml");
        if (!plantilla.exists()) {
            throw new FileNotFoundException("No se encontro la plantilla reportes/grafico_modos_riego.jrxml");
        }

        try (InputStream reporteStream = plantilla.getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(datosReporte);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("creadoPor", "Sistema SRI - Administrador");
            parametros.put("fechaGeneracion", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            parametros.put("TITULO_FILTRO", obtenerTituloFiltro(filtro));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (IOException e) {
            FileNotFoundException exception = new FileNotFoundException("No se pudo leer la plantilla grafico_modos_riego.jrxml");
            exception.initCause(e);
            throw exception;
        }
    }

    private List<Map<String, Object>> obtenerFilasReporte(LocalDateTime inicio, LocalDateTime fin, FiltroReporte filtro) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    e.id AS id,
                    e.fecha_inicio AS fechaInicio,
                    COALESCE(c.nombre, 'Mantenimiento / Sin Cultivo') AS nombreCultivo,
                    e.modo_riego AS modoRiego,
                    e.humedad_suelo_inicial AS humedadSueloInicial,
                    e.humedad_suelo_final AS humedadSueloFinal,
                    e.estado AS estado,
                    1 AS cantidad
                FROM eventos_riego e
                LEFT JOIN perfiles_cultivo c ON c.id = e.cultivo_id
                WHERE 1 = 1
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (inicio != null) {
            sql.append(" AND e.fecha_inicio >= :fechaInicio");
            params.addValue("fechaInicio", inicio);
        }

        if (fin != null) {
            sql.append(" AND e.fecha_inicio < :fechaFin");
            params.addValue("fechaFin", fin);
        }

        if (filtro.soloMantenimiento()) {
            sql.append(" AND e.cultivo_id IS NULL");
        } else if (filtro.cultivoId() != null) {
            sql.append(" AND e.cultivo_id = :cultivoId");
            params.addValue("cultivoId", filtro.cultivoId());
        }

        sql.append(" ORDER BY e.fecha_inicio DESC");

        return jdbcTemplate.queryForList(sql.toString(), params);
    }

    private Map<String, Object> toReporteRow(Map<String, Object> fila) {
        Map<String, Object> row = new HashMap<>();

        row.put("id", valor(fila, "id"));
        row.put("fechaInicio", formatearFecha(valor(fila, "fechaInicio", "fecha_inicio")));
        row.put("nombreCultivo", texto(valor(fila, "nombreCultivo", "nombre_cultivo"), NOMBRE_MANTENIMIENTO));
        row.put("modoRiego", texto(valor(fila, "modoRiego", "modo_riego"), "-"));
        row.put("humedadSueloInicial", valor(fila, "humedadSueloInicial", "humedad_suelo_inicial") != null
                ? valor(fila, "humedadSueloInicial", "humedad_suelo_inicial")
                : "-");
        row.put("humedadSueloFinal", valor(fila, "humedadSueloFinal", "humedad_suelo_final") != null
                ? valor(fila, "humedadSueloFinal", "humedad_suelo_final")
                : "-");
        row.put("estado", texto(valor(fila, "estado"), "-"));
        row.put("cantidad", valor(fila, "cantidad") != null ? valor(fila, "cantidad") : 1);

        return row;
    }

    private Object valor(Map<String, Object> fila, String... claves) {
        for (String clave : claves) {
            if (fila.containsKey(clave)) {
                return fila.get(clave);
            }
        }

        return null;
    }

    private String texto(Object valor, String valorPorDefecto) {
        return valor != null ? String.valueOf(valor) : valorPorDefecto;
    }

    private String formatearFecha(Object valor) {
        if (valor == null) {
            return "-";
        }

        if (valor instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        if (valor instanceof LocalDateTime fecha) {
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        return String.valueOf(valor);
    }

    private FiltroReporte resolverFiltro(String cultivoId) {
        if (cultivoId == null || cultivoId.isBlank()) {
            return new FiltroReporte(null, false);
        }

        if (CULTIVO_MANTENIMIENTO.equalsIgnoreCase(cultivoId.trim())) {
            return new FiltroReporte(null, true);
        }

        return new FiltroReporte(Integer.valueOf(cultivoId), false);
    }

    private String obtenerTituloFiltro(FiltroReporte filtro) {
        if (filtro.soloMantenimiento()) {
            return NOMBRE_MANTENIMIENTO;
        }

        if (filtro.cultivoId() != null) {
            return perfilCultivoRepository.findById(filtro.cultivoId())
                    .map(Cultivo::getNombre)
                    .orElse("Cultivo " + filtro.cultivoId());
        }

        return "Reporte General";
    }

    private record FiltroReporte(Integer cultivoId, boolean soloMantenimiento) {
    }
}
