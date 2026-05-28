package com.locadora_rdt_backend.modules.positions.tracing;

public final class PositionTracingConstants {

    private PositionTracingConstants() {
    }

    public static final String MODULE = "positions";
    public static final String RESOURCE = "positions";

    public static final String OPERATION_FIND_ALL = "find_all";
    public static final String OPERATION_FIND_BY_ID = "find_by_id";
    public static final String OPERATION_CREATE = "create";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";

    public static final String OPERATION_REPOSITORY_SEARCH_BY_NAME = "repository_search_by_name";
    public static final String OPERATION_REPOSITORY_FIND_BY_ID = "repository_find_by_id";
    public static final String OPERATION_REPOSITORY_SAVE = "repository_save";
    public static final String OPERATION_REPOSITORY_DELETE = "repository_delete";
    public static final String OPERATION_REPOSITORY_FLUSH = "repository_flush";

    public static final String OPERATION_MAPPER_TO_DTO = "mapper_to_dto";
    public static final String OPERATION_MAPPER_TO_DETAILS_DTO = "mapper_to_details_dto";
    public static final String OPERATION_MAPPER_TO_ENTITY = "mapper_to_entity";
    public static final String OPERATION_MAPPER_COPY_TO_ENTITY = "mapper_copy_to_entity";

    public static final String OPERATION_VALIDATE_CREATE = "validate_create";
    public static final String OPERATION_VALIDATE_UPDATE = "validate_update";
    public static final String OPERATION_VALIDATE_DELETE = "validate_delete";

    public static final String OPERATION_AUDIT_CREATE = "audit_create";
    public static final String OPERATION_AUDIT_UPDATE = "audit_update";
    public static final String OPERATION_AUDIT_DELETE = "audit_delete";

    public static final String OPERATION_METRICS_RECORD = "metrics_record";
}