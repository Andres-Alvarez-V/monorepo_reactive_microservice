package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.dto.CapacityTechnologyCreateRequestDTO;
import co.com.bancolombia.consumer.dto.CapacityWithTechnologiesByCapacityIdListResDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyConsumer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CapacityTechnologyRestConsumer implements TechnologyConsumer {
    private final WebClient client;

    @CircuitBreaker(name = "createCapacityTechnologies")
    public Mono<Void> createCapacityTechnologies(Capacity capacity, List<Technology> technologies) {
        CapacityTechnologyCreateRequestDTO request = CapacityTechnologyCreateRequestDTO.builder()
                .idCapacidad(capacity.getId())
                .idTecnologias(technologies.stream().map(Technology::getId).toList())
                .build();
        return client.post()
                .uri("/api/capacity-technology")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CapacityTechnologyCreateRequestDTO.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @CircuitBreaker(name = "getTechnologiesByCapacityIds")
    public Flux<CapacityTechnologies> getTechnologiesByCapacityIds(List<Long> capacityIds) {
        String capacityIdsParam = String.join(",", capacityIds.stream().map(String::valueOf).toList());
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/capacity-technologies-by-capicity-id")
                        .queryParam("idsCapacidad", capacityIdsParam)
                        .build())
                .retrieve()
                .bodyToFlux(CapacityWithTechnologiesByCapacityIdListResDTO.class)
                .map(this::convertToModel)
                .onErrorResume(throwable -> Mono.error(new RuntimeException("Erro getTechnologiesByCapacityIds " + throwable.toString())));
    }

    @CircuitBreaker(name = "getCapacitiesWithTechnologiesOrderedByTechnologyCount")
    public Mono<PageResponse<CapacityTechnologies>> getCapacitiesWithTechnologiesOrderedByTechnologyCount(Pagination pagination) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/capacity-technologies-by-technology-count")
                        .queryParam("offset", pagination.getOffset())
                        .queryParam("limit", pagination.getLimit())
                        .queryParam("sortBy", pagination.getSortBy())
                        .queryParam("sortDirection", pagination.getSortDirection())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<CapacityWithTechnologiesByCapacityIdListResDTO>>() {})
                .map(pageResponseDTO -> {
                    List<CapacityTechnologies> modelList = pageResponseDTO.getContent().stream()
                            .map(this::convertToModel)
                            .collect(Collectors.toList());
                    return PageResponse.<CapacityTechnologies>builder()
                            .content(modelList)
                            .totalElements(pageResponseDTO.getTotalElements())
                            .totalPages(pageResponseDTO.getTotalPages())
                            .currentPage(pageResponseDTO.getCurrentPage())
                            .pageSize(pageResponseDTO.getPageSize())
                            .build();
                });
    }

    private CapacityTechnologies convertToModel(CapacityWithTechnologiesByCapacityIdListResDTO dto) {
        Capacity capacity = Capacity.builder().id(dto.getIdCapacidad()).build();
        List<Technology> technologies = dto.getTecnologias().stream()
                .map(technologyDTO -> Technology.builder()
                        .id(technologyDTO.getId())
                        .name(technologyDTO.getNombre())
                        .build())
                .collect(Collectors.toList());
        return CapacityTechnologies.builder()
                .capacity(capacity)
                .technologies(technologies)
                .build();
    }
}