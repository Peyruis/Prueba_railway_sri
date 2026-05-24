/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sri.project.sri_project.service.serviceImpl;

import sri.project.sri_project.service.EstadisticasService;
import sri.project.sri_project.repository.EstadisticasRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * @author Usuario
 */



@Service
public class VisualizarEstadisticasServiceImpl implements EstadisticasService {

    private final EstadisticasRepository repository;

    public VisualizarEstadisticasServiceImpl(EstadisticasRepository repository) {
        this.repository = repository;
    }
    @Override
    public Map<String, Integer> obtenerDatosHumedadHistorica() {
        return repository.obtenerHumedadPromedioPorHora();
    }

    @Override
    public Map<String, Integer> obtenerDatosModosRiego() {
        return repository.obtenerConteoModosRiego();
    }
}
