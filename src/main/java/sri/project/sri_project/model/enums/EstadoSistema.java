package sri.project.sri_project.model.enums;

public enum EstadoSistema {
    REGANDO,
    ESPERA,
    BLOQUEADO_SIN_AGUA;

    public String descripcion() {
        switch (this) {
            case REGANDO:
                return "Regando";
            case ESPERA:
                return "Humedad OK";
            case BLOQUEADO_SIN_AGUA:
                return "Bloqueado. Sin agua";
            default:
                return "";
        }
    }


}
