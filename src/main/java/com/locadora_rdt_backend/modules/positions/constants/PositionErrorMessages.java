package com.locadora_rdt_backend.modules.positions.constants;

public final class PositionErrorMessages {

    public static final String POSITION_NOT_FOUND =
            "Cargo não encontrado";

    public static final String DATABASE_INTEGRITY_VIOLATION =
            "Violação de integridade no banco de dados";

    public static final String POSITION_IN_USE =
            "Não é possível excluir este cargo, pois ele está associado a um ou mais empregados.";

    private PositionErrorMessages() {
    }
}