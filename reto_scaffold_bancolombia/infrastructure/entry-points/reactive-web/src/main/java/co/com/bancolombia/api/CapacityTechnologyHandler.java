package co.com.bancolombia.api;


import co.com.bancolombia.api.dto.capacityTechnology.CapacityTechnologyCreateRequestDTO;
import co.com.bancolombia.api.dto.capacityTechnology.CapacityWithTechByCountTechListReqDTO;
import co.com.bancolombia.api.mapper.CapacityTechnologyMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.capacitytechnology.CapacityTechnologyUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CapacityTechnologyHandler {
    private final CapacityTechnologyUseCase capacityTechnologyUseCase;
    private final Validator validator;

    public Mono<ServerResponse> createCapacityTechnology(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CapacityTechnologyCreateRequestDTO.class)
                .flatMap(capacityTechnologyDTO -> {
                    Set<ConstraintViolation<CapacityTechnologyCreateRequestDTO>> violations = validator.validate(capacityTechnologyDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage =
                                violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }
                    return capacityTechnologyUseCase.createCapacityTechnology(
                                CapacityTechnologyMapper.mapCapacityTechnologyRequestDTOToListCapacityTechnology(capacityTechnologyDTO)
                            )
                            .collectList()
                            .flatMap(createdCapacityTechnology ->
                            ServerResponse.created(URI.create("/api/capacity-technology"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(createdCapacityTechnology)
                    );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }

    public Mono<ServerResponse> findCapacityWithTechnologieByCapacityIdIn(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> {
                        String capacityIds = request.queryParam("idsCapacidad")
                                .orElseThrow(() -> new BusinessException("idsCapacidad is required", TechnicalMessage.INVALID_REQUEST));
                        return Arrays.stream(capacityIds.split(","))
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                    }
                )
                .flatMapMany(capacityTechnologyUseCase::findCapacityWithTechnologieByCapacityIdIn)
                .map(CapacityTechnologyMapper::mapCapacityWithTechnologiesListResponse) //
                .collectList()
                .flatMap(capacityWithTechnologies ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(capacityWithTechnologies))
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }

    public Mono<ServerResponse> findAllCapacitiesWithTechnologiesOrderedByTechnologyCount (ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(req -> {
                    CapacityWithTechByCountTechListReqDTO requestDTO = new CapacityWithTechByCountTechListReqDTO();

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
                    Set<ConstraintViolation<CapacityWithTechByCountTechListReqDTO>> violations = validator.validate(requestDTO);
                    if (!violations.isEmpty()) {
                        String errorMessage =
                                violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }

                    Pagination pagination = Pagination.builder()
                            .limit(requestDTO.getLimit())
                            .offset(requestDTO.getOffset())
                            .sortDirection(requestDTO.getSortDirection())
                            .build();

                    return capacityTechnologyUseCase.findAllCapacitiesWithTechnologiesOrderedByTechnologyCount(pagination)
                            .map(CapacityTechnologyMapper::mapAllCapacitiesWithTehcnologiesOrderByTechnologyCountResponse)
                            .flatMap(capacityWithTechnologies ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(capacityWithTechnologies));
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }
}
