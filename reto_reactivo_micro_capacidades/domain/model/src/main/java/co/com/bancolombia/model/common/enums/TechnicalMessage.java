package co.com.bancolombia.model.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    INTERNAL_ERROR(500,"Something went wrong, please try again", ""),
    INVALID_REQUEST(400, "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    INVALID_EMAIL(403, "Invalid email, please verify", "email"),
    INVALID_MESSAGE_ID(404, "Invalid Message ID, please verify", "messageId"),
    UNSUPPORTED_OPERATION(501, "Method not supported, please try again", ""),
    USER_CREATED(201, "User created successfully", ""),
    ADAPTER_RESPONSE_NOT_FOUND(404, "invalid email, please verify", ""),
    ERROR_CREATING_CAPACITY(500, "Error creando la capacidad", ""),
    ERROR_CREATING_CAPACITY_WITH_TECHNOLOGIES(500, "Error creando la capacidad con tecnologias", ""),
    TECHNOLOGY_ALREADY_EXISTS(400,"La tecnologia ya está registrada." ,"" );



    private final Integer
            code;
    private final String message;
    private final String param;
}
