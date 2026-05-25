package com.locadora_rdt_backend.modules.positions.logging;

public final class PositionLogMessages {

    public static final String POSITION_SEARCH_STARTED =
            "Iniciando busca paginada de cargos";

    public static final String POSITION_SEARCH_FINISHED =
            "Busca paginada de cargos concluída";

    public static final String POSITION_DETAILS_STARTED =
            "Iniciando busca de detalhes do cargo";

    public static final String POSITION_DETAILS_FINISHED =
            "Busca de detalhes do cargo concluída";

    public static final String POSITION_CREATED =
            "Cargo criado com sucesso";

    public static final String POSITION_UPDATED =
            "Cargo atualizado com sucesso";

    public static final String POSITION_DELETED =
            "Cargo removido com sucesso";

    public static final String POSITION_DELETE_FAILED =
            "Falha ao remover cargo";

    private PositionLogMessages() {
    }
}
