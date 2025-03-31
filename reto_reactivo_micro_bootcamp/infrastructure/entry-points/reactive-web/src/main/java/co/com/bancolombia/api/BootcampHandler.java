package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.bootcamp.BootcampCreateRequestDTO;
import co.com.bancolombia.api.mapper.BootcampMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.bootcamp.BootcampUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BootcampHandler {

    private final Validator validator;
    private final BootcampUseCase bootcampUseCase;

    public Mono<ServerResponse> createBootcampWithCapacities(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BootcampCreateRequestDTO.class)
                .flatMap(requestDTO -> {
                    Set<ConstraintViolation<BootcampCreateRequestDTO>> violations = validator.validate(requestDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }

                    Bootcamp bootcamp = BootcampMapper.mapBootcampCreateRequestDTOToBootcamp(requestDTO);
                    List<Capacity> capacities = BootcampMapper.mapIdCapacidadesToCapacities(requestDTO.getIdCapacidades());

                    return bootcampUseCase.createBootcampWithCapacities(bootcamp, capacities)
                            .flatMap(createdBootcamp ->
                                    ServerResponse.created(URI.create("/api/bootcamp/" + createdBootcamp.getId()))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(createdBootcamp)
                            );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }


}
