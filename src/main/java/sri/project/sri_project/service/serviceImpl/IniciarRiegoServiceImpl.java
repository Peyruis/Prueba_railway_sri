/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sri.project.sri_project.service.serviceImpl;


import sri.project.sri_project.service.IniciarRiegoService;
import sri.project.sri_project.integration.ControlRiego;
import sri.project.sri_project.model.EstadoSistema;
import sri.project.sri_project.model.TanqueAgua;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Usuario
 */


@Service
@Transactional
public class IniciarRiegoServiceImpl implements IniciarRiegoService {

    private final ControlRiego riegoPort;
    private final TanqueAgua tanque;

    private static final int INICIAR_RIEGO = 1;


    public IniciarRiegoServiceImpl(ControlRiego riegoPort, TanqueAgua tanque) {
        this.riegoPort = riegoPort;
        this.tanque = tanque;

    }


    @Override
    public String ejecutar() {

        if (tanque.getEstadoActual() == EstadoSistema.BLOQUEADO_SIN_AGUA) {
            return "El tanque no tiene agua suficiente para regar.";
        }


        tanque.recordarHumedadInicial();


        riegoPort.enviarComando(INICIAR_RIEGO);

        tanque.activarRiego();
        tanque.setEstadoActual(EstadoSistema.REGANDO);

        return "Riego iniciado";
    }


}
