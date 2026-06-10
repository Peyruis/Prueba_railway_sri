package sri.project.sri_project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sri.project.sri_project.model.EventoRiego;
import sri.project.sri_project.model.enums.EstadoRiego;
import sri.project.sri_project.model.enums.ModoRiego;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRiegoRepository extends JpaRepository<EventoRiego,Long> {


// ---------------------------------------------------------
    // GRÁFICO 1: Dona/Pastel (Manual vs Automático del mes actual)
    // Usamos JPQL para contar directamente las entidades
    // ---------------------------------------------------------


    /*
    *
    * Para el grafico de Dona.
    * Comparativa de manual y automatico en el mes
    * */

    @Query("SELECT e.modoRiego, COUNT(e)" +
            " FROM EventoRiego e " +
            "WHERE FUNCTION('MONTH', e.fechaInicio) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', e.fechaInicio) = FUNCTION('YEAR', CURRENT_DATE) " +
            "GROUP BY e.modoRiego")
    List<Object[]> contarRiegosPorModoMesActual();

    @Query("SELECT e.modoRiego, COUNT(e) " +
            "FROM EventoRiego e " +
            "WHERE (:fechaInicio IS NULL OR e.fechaInicio >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR e.fechaInicio < :fechaFin) " +
            "GROUP BY e.modoRiego")
    List<Object[]> contarRiegosPorModoEntreFechas(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                  @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT e.modoRiego, COALESCE(c.nombre, 'Riego de Mantenimiento'), COUNT(e) " +
            "FROM EventoRiego e " +
            "LEFT JOIN e.cultivo c " +
            "WHERE (:fechaInicio IS NULL OR e.fechaInicio >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR e.fechaInicio < :fechaFin) " +
            "GROUP BY e.modoRiego, c.nombre")
    List<Object[]> contarRiegosPorModoYCultivoEntreFechas(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                          @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT e " +
            "FROM EventoRiego e " +
            "LEFT JOIN FETCH e.cultivo c " +
            "WHERE (:fechaInicio IS NULL OR e.fechaInicio >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR e.fechaInicio < :fechaFin) " +
            "AND (:soloMantenimiento = false OR e.cultivo IS NULL) " +
            "AND (:cultivoId IS NULL OR c.id = :cultivoId) " +
            "ORDER BY e.fechaInicio DESC")
    List<EventoRiego> buscarEventosParaReporte(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin,
                                                @Param("cultivoId") Integer cultivoId,
                                                @Param("soloMantenimiento") boolean soloMantenimiento);

    @Query(value = "SELECT e.fecha_inicio, " +
            "COALESCE(c.nombre, 'Mantenimiento / Sin Cultivo') AS nombre_cultivo, " +
            "e.modo_riego, " +
            "e.humedad_suelo_inicial, " +
            "e.humedad_suelo_final, " +
            "e.estado " +
            "FROM eventos_riego e " +
            "LEFT JOIN perfiles_cultivo c ON c.id = e.cultivo_id " +
            "WHERE (:fechaInicio IS NULL OR e.fecha_inicio >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR e.fecha_inicio < :fechaFin) " +
            "AND (:soloMantenimiento = false OR e.cultivo_id IS NULL) " +
            "AND (:cultivoId IS NULL OR e.cultivo_id = :cultivoId) " +
            "ORDER BY e.fecha_inicio DESC", nativeQuery = true)
    List<Object[]> buscarFilasParaReporte(@Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin,
                                           @Param("cultivoId") Integer cultivoId,
                                           @Param("soloMantenimiento") boolean soloMantenimiento);



    // GRÁFICO 2: Eficiencia (Humedad ganada en riegos completados)
    // JPQL puro aprovechando que cambiamos a valores INT

    @Query("SELECT e.fechaInicio, (e.humedadSueloFinal - e.humedadSueloInicial) FROM EventoRiego e " +
            "WHERE e.estado = 'COMPLETADO' AND e.cultivo.id = :cultivoId " +
            "ORDER BY e.fechaInicio DESC")
    List<Object[]> calcularHumedadGanadaPorCultivo(@Param("cultivoId") Integer cultivoId);




    // GRÁFICO 3: Barras (Consumo de tiempo de riego de los últimos 7 días)
    // Usamos Consulta Nativa para usar TIMESTAMPDIFF de MySQL

    @Query(value = "SELECT DATE(fecha_inicio) as fecha, SUM(TIMESTAMPDIFF(SECOND, fecha_inicio, fecha_fin)) as duracion_segundos " +
            "FROM eventos_riego " +
            "WHERE fecha_inicio >= NOW() - INTERVAL 7 DAY AND estado = 'COMPLETADO' " +
            "GROUP BY DATE(fecha_inicio) " +
            "ORDER BY fecha", nativeQuery = true)
    List<Object[]> obtenerDuracionDiariaUltimos7Dias();

    long countByEstado(EstadoRiego estado);

    long countByModoRiegoAndFechaInicioBetween(ModoRiego modoRiego, LocalDateTime inicio, LocalDateTime fin);

    long countByModoRiegoAndCultivo_IdAndFechaInicioBetween(ModoRiego modoRiego,
                                                            Integer cultivoId,
                                                            LocalDateTime inicio,
                                                            LocalDateTime fin);

    long countByModoRiegoAndCultivoIsNullAndFechaInicioBetween(ModoRiego modoRiego,
                                                               LocalDateTime inicio,
                                                               LocalDateTime fin);

    Optional<EventoRiego> findFirstByEstadoOrderByFechaInicioDesc(EstadoRiego estado);

    EventoRiego findTopByOrderByFechaInicioDesc();

    @Query(value = "SELECT COALESCE(AVG(humedad_suelo_final - humedad_suelo_inicial), 0) " +
            "FROM eventos_riego " +
            "WHERE estado = 'COMPLETADO' AND humedad_suelo_final IS NOT NULL", nativeQuery = true)
    Double obtenerPromedioHumedadGanada();



}
