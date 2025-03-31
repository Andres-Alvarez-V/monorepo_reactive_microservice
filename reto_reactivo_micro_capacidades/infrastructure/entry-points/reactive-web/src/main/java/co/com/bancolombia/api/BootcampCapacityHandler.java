package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.api.mapper.BootcampCapacityMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.bootcampCapacity.BootcampCapacityUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BootcampCapacityHandler {
    private final Validator validator;
    private final BootcampCapacityUseCase bootcampCapacityUseCase;

    public Mono<ServerResponse> createBootcampCapacity(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootcampCapacitiesCreateRequestDTO.class)
                .flatMap(capacityTechnologyDTO -> {
                    Set<ConstraintViolation<BootcampCapacitiesCreateRequestDTO>> violations = validator.validate(capacityTechnologyDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage =
                                violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }
                    return bootcampCapacityUseCase.createBootcampCapacity(
                                    BootcampCapacityMapper.mapBootcampCapacityRequestDTOToListBootcampCapacity(capacityTechnologyDTO)
                            )
                            .collectList()
                            .flatMap(createdCapacityTechnology ->
                                    ServerResponse.created(URI.create("/api/bootcamp-capacity"))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(createdCapacityTechnology)
                            );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }
}
