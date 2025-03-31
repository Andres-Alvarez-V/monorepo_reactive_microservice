package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.capacity.CapacityCreateRequestDTO;
import co.com.bancolombia.api.dto.capacity.CapacityTechnologiesListRequestDTO;
import co.com.bancolombia.api.mapper.CapacityMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.usecase.capacity.CapacityUseCase;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CapacityHandler {

    private final CapacityUseCase capacityUseCase;
    private final Validator validator;


    public Mono<ServerResponse> createCapacity(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapacityCreateRequestDTO.class)
                .flatMap(requestDTO -> {
                    Set<ConstraintViolation<CapacityCreateRequestDTO>> violations = validator.validate(requestDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage = violations.stream()
                                .map(ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }


                    Capacity capacity = CapacityMapper.mapCapacityCreateRequestDTOToCapacity(requestDTO);
                    List<Technology> technologies = CapacityMapper.mapIdTecnologiasToTechnologies(requestDTO.getIdTecnologias());

                    return capacityUseCase.createCapacityWithTechnologies(capacity, technologies)
                            .flatMap(createdCapacity ->
                                    ServerResponse.created(URI.create("/api/capacity/" + createdCapacity.getId()))
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(createdCapacity)
                            );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }

    public Mono<ServerResponse> listCapacityTechnologies(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(req -> {
                    CapacityTechnologiesListRequestDTO requestDTO = new CapacityTechnologiesListRequestDTO();

                    Optional<String> limitParam = req.queryParam("limit");
                    Optional<String> offsetParam = req.queryParam("offset");
                    Optional<String> sortByParam = req.queryParam("sortBy");
                    Optional<String> sortDirectionParam = req.queryParam("sortDirection");

                    requestDTO.setLimit(limitParam.map(Integer::parseInt).orElse(10));
                    requestDTO.setOffset(offsetParam.map(Integer::parseInt).orElse(0));
                    requestDTO.setSortBy(sortByParam.orElse("nombre"));
                    requestDTO.setSortDirection(sortDirectionParam.orElse("asc"));
                    return requestDTO;
                })
                .flatMap(requestDTO -> {
                    Set<ConstraintViolation<CapacityTechnologiesListRequestDTO>> violations = validator.validate(requestDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage =
                                violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }

                    String dbSortBy = requestDTO.getSortBy().equals("nombre") ? "name" : requestDTO.getSortBy();

                    Pagination pagination = Pagination.builder()
                            .limit(requestDTO.getLimit())
                            .offset(requestDTO.getOffset())
                            .sortBy(dbSortBy)
                            .sortDirection(requestDTO.getSortDirection())
                            .build();

                    return capacityUseCase.listCapacityWithTechnologies(pagination)
                            .map(CapacityMapper::mapPageResponse)
                            .flatMap(technologyResponseDTOs ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(technologyResponseDTOs)
                            );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }
}
