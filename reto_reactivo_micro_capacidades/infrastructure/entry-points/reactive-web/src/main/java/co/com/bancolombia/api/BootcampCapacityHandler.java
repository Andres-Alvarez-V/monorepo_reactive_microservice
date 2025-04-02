package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacityByCountListReqDTO;
import co.com.bancolombia.api.mapper.BootcampCapacityMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.Pagination;
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
import java.util.Arrays;
import java.util.Optional;
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

    public Mono<ServerResponse> findBootcampWithCapacitiesByCapacityIdIn(ServerRequest serverRequest){
        return Mono.just(serverRequest)
                .map(request -> {
                            String capacityIds = request.queryParam("idsBootcamp")
                                    .orElseThrow(() -> new BusinessException("idsBootcamp is required", TechnicalMessage.INVALID_REQUEST));
                            return Arrays.stream(capacityIds.split(","))
                                    .map(Long::parseLong)
                                    .collect(Collectors.toList());
                        }
                )
                .flatMap(bootcampCapacityUseCase::findBootcampWithCapacitiesByCapacityIdIn)
                .map(BootcampCapacityMapper::mapBootcampWithCapacitiesListResponse)
                .flatMap(bootcampWithCapacities ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(bootcampWithCapacities))
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }

    public Mono<ServerResponse>  findAllBootcampWithCapacityOrderedByCapacityCount (ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(req -> {
                    BootcampCapacityByCountListReqDTO requestDTO = new BootcampCapacityByCountListReqDTO();

                    Optional<String> limitParam = req.queryParam("limit");
                    Optional<String> offsetParam = req.queryParam("offset");
                    Optional<String> sortByParam = req.queryParam("sortBy");
                    Optional<String> sortDirectionParam = req.queryParam("sortDirection");

                    requestDTO.setLimit(limitParam.map(Integer::parseInt).orElse(10));
                    requestDTO.setOffset(offsetParam.map(Integer::parseInt).orElse(0));
                    requestDTO.setSortBy(sortByParam.orElse("cantidadcapacidades"));
                    requestDTO.setSortDirection(sortDirectionParam.orElse("asc"));
                    return requestDTO;
                })
                .flatMap(requestDTO -> {
                    Set<ConstraintViolation<BootcampCapacityByCountListReqDTO>> violations = validator.validate(requestDTO);
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

                    return bootcampCapacityUseCase.findAllBootcampWithCapacitiesOrderedByCapacityCount(pagination)
                            .map(BootcampCapacityMapper::mapAllBootcampWithCapacityOrderByCapacityCountResponse)
                            .flatMap(capacityWithTechnologies ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(capacityWithTechnologies));
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }
}
