package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.technology.TechnologyDTO;
import co.com.bancolombia.api.dto.technology.TechnologyListRequestDTO;
import co.com.bancolombia.api.mapper.TechnologyMapper;
import co.com.bancolombia.api.util.ErrorHandler;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.usecase.technology.TechnologyUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TechnologyHandler {

    private final TechnologyUseCase technologyUseCase;
    private final Validator validator;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> createTechnology(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TechnologyDTO.class)
                .flatMap(technologyDTO -> {
                    Set<ConstraintViolation<TechnologyDTO>> violations = validator.validate(technologyDTO);
                    if (!violations.isEmpty()) {
                         String errorMessage =
                                violations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                        return Mono.error(new BusinessException(errorMessage, TechnicalMessage.INVALID_REQUEST));
                    }
                    return technologyUseCase.createTechnology(
                            TechnologyMapper.mapTechnologyDTOToTechnology(technologyDTO)
                    ).flatMap(createdTechnology ->
                            ServerResponse.created(URI.create("/api/technology/" + createdTechnology.getId()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(createdTechnology)
                    );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }

    public Mono<ServerResponse> listTechnologies(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(req -> {
                    TechnologyListRequestDTO requestDTO = new TechnologyListRequestDTO();

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
                    Set<ConstraintViolation<TechnologyListRequestDTO>> violations = validator.validate(requestDTO);
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

                    return technologyUseCase.listTechnologies(pagination)
                            .map(TechnologyMapper::mapPageResponse)
                            .flatMap(technologyResponseDTOs ->
                                    ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(technologyResponseDTOs)
                            );
                })
                .onErrorResume(ex -> ErrorHandler.mapError(ex, "messageId"));
    }
}
