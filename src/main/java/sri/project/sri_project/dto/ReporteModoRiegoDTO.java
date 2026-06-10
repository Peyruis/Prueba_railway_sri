package sri.project.sri_project.dto;

import sri.project.sri_project.model.enums.ModoRiego;

public class ReporteModoRiegoDTO {

    private final ModoRiego modoRiego;
    private final String cultivoNombre;
    private final Long cantidad;

    public ReporteModoRiegoDTO(ModoRiego modoRiego, String cultivoNombre, Long cantidad) {
        this.modoRiego = modoRiego;
        this.cultivoNombre = cultivoNombre;
        this.cantidad = cantidad;
    }

    public ModoRiego getModoRiego() {
        return modoRiego;
    }

    public String getCultivoNombre() {
        return cultivoNombre;
    }

    public Long getCantidad() {
        return cantidad;
    }
}
