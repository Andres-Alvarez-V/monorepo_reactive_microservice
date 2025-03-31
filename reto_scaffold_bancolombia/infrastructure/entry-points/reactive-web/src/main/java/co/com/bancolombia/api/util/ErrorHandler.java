package co.com.bancolombia.api.util;

import co.com.bancolombia.api.dto.ErrorDTO;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public class ErrorHandler {

    public static Mono<ServerResponse> buildErrorResponse(Integer status, String messageId, TechnicalMessage technicalMessage, List<ErrorDTO> errors) {
        return ServerResponse.status(status)
                .bodyValue(errors);
    }

    public static Mono<ServerResponse> mapError(Throwable ex, String messageId) {
        if (ex instanceof BusinessException businessException) {
            return buildErrorResponse(
                    businessException.getTechnicalMessage().getCode(),
                    messageId,
                    businessException.getTechnicalMessage(),
                    List.of(ErrorDTO.builder()
                            .code(businessException.getTechnicalMessage().getCode())
                            .message(businessException.getTechnicalMessage().getMessage())
                            .param(businessException.getMessage())
                            .build()));
        } else if (ex instanceof TechnicalException technicalException) {
            return buildErrorResponse(
                    technicalException.getTechnicalMessage().getCode(),
                    messageId,
                    technicalException.getTechnicalMessage(),
                    List.of(ErrorDTO.builder()
                            .code(technicalException.getTechnicalMessage().getCode())
                            .message(technicalException.getTechnicalMessage().getMessage())
                            .param(technicalException.getTechnicalMessage().getParam())
                            .build()));
        } else {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    messageId,
                    TechnicalMessage.INTERNAL_ERROR,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                            .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                            .build()));
        }
    }
}
