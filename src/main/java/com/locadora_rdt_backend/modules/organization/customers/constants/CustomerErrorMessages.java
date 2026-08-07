package com.locadora_rdt_backend.modules.organization.customers.constants;

public final class CustomerErrorMessages {

    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado";
    public static final String FILE_NOT_FOUND = "Arquivo não encontrado";
    public static final String ID_NOT_FOUND = "Id not found ";
    public static final String CUSTOMER_NOT_FOUND_WITH_ID = "Cliente não encontrado. Id: ";
    public static final String FILE_NOT_FOUND_WITH_ID = "Arquivo não encontrado. Id: ";
    public static final String FILE_DOES_NOT_BELONG_TO_CUSTOMER =
            "Arquivo não pertence ao cliente informado.";
    public static final String IMAGE_READ_ERROR = "Falha ao ler bytes do arquivo.";
    public static final String EMPTY_ID_LIST = "Lista de ids vazia";
    public static final String ONE_OR_MORE_IDS_NOT_FOUND = "Um ou mais IDs não existem";
    public static final String CHANGE_ACTIVE_STATUS_ERROR = "Error changing customer status.";

    private CustomerErrorMessages() {
    }
}
