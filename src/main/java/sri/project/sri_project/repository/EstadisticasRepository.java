
package sri.project.sri_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface EstadisticasRepository extends JpaRepository<Map<String,Double>,Integer> {



    Map<String, Integer> obtenerHumedadPromedioPorHora();

    Map<String, Integer> obtenerConteoModosRiego();

    void guardarLecturaSensores(int humedad, int distancia);

    void registrarSesionRiego(String modo, int humedadInicial, int humedadFinal, String motivo);
}
