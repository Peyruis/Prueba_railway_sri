/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sri.project.sri_project.service.serviceImpl;


import sri.project.sri_project.service.DetenerRiegoService;
import sri.project.sri_project.repository.EstadisticasRepository;
import sri.project.sri_project.integration.ControlRiego;
import sri.project.sri_project.model.EstadoSistema;
import sri.project.sri_project.model.TanqueAgua;

import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */


@Service
public class DetenerRiegoServiceImpl implements DetenerRiegoService {
    
    
    private final ControlRiego port;
    private final TanqueAgua tanque;

    private EstadisticasRepository repository;

    private static final int DETENER_RIEGO = 0;

    public DetenerRiegoServiceImpl(ControlRiego port, TanqueAgua tanque, EstadisticasRepository repository) {
        this.port = port;
        this.tanque = tanque;
        this.repository = repository;
    }
    


    @Override
    public String ejecutar() {


        if (!tanque.isBombaActiva()) {
            return "La bomba ya está detenida";
        }


        port.enviarComando(DETENER_RIEGO);

        tanque.detenerRiego();


        /*
        * Domain logic
        * class
        * - tanque
        * */

        if (tanque.getEstadoActual() != EstadoSistema.BLOQUEADO_SIN_AGUA) {
            repository.registrarSesionRiego(
                    "MANUAL",
                    tanque.getHumedadAlIniciarManual(),
                    tanque.getHumedad(),
                    "APAGADO_MANUAL"
            );
        }


        return "Riego detenido y registrado correctamente";
    }


    
}
