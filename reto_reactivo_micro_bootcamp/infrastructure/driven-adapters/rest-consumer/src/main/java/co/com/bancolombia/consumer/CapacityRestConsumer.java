package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.dto.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.consumer.dto.BootcampWithCapacitiesByBootcampIdListResDTO;
import co.com.bancolombia.consumer.dto.CapacityWithTechnologiesByCapacityIdDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.BootcampCapacities;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.capacity.gateways.CapacityConsumer;
import co.com.bancolombia.model.technology.Technology;
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
public class CapacityRestConsumer implements CapacityConsumer {
    private final WebClient client;


    @CircuitBreaker(name = "createCapacityTechnologies")
    public Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        BootcampCapacitiesCreateRequestDTO request = BootcampCapacitiesCreateRequestDTO.builder()
                .idBootcamp(bootcamp.getId())
                .idCapacidades(capacities.stream().map(Capacity::getId).toList())
                .build();
        return client.post()
                .uri("/api/bootcamp-capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), BootcampCapacitiesCreateRequestDTO.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @CircuitBreaker(name = "getCapacitiesByBootcampIds")
    public Flux<BootcampCapacities> getCapacitiesByBootcampIds(List<Long> bootcampIds) {
        String capacityIdsParam = String.join(",", bootcampIds.stream().map(String::valueOf).toList());
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/bootcamp-capacities-by-bootcamp-id")
                        .queryParam("idsBootcamp", capacityIdsParam)
                        .build())
                .retrieve()
                .bodyToFlux(BootcampWithCapacitiesByBootcampIdListResDTO.class)
                .map(this::convertBootcampWithCapacitiesDTOToModel);
    }

    @CircuitBreaker(name = "getBootcampCapacitiesOrderedByCapacityCount")
    public Mono<PageResponse<BootcampCapacities>> getBootcampCapacitiesOrderedByCapacityCount(Pagination pagination) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/bootcamp-capacities-by-capacity-count")
                        .queryParam("offset", pagination.getOffset())
                        .queryParam("limit", pagination.getLimit())
                        .queryParam("sortBy", pagination.getSortBy())
                        .queryParam("sortDirection", pagination.getSortDirection())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<BootcampWithCapacitiesByBootcampIdListResDTO>>() {})
                .map(pageResponseDTO -> {
                    List<BootcampCapacities> modelList = pageResponseDTO.getContent().stream()
                            .map(this::convertBootcampWithCapacitiesDTOToModel)
                            .toList();
                    return PageResponse.<BootcampCapacities>builder()
                            .content(modelList)
                            .totalElements(pageResponseDTO.getTotalElements())
                            .totalPages(pageResponseDTO.getTotalPages())
                            .currentPage(pageResponseDTO.getCurrentPage())
                            .pageSize(pageResponseDTO.getPageSize())
                            .build();
                });

    }

    private BootcampCapacities convertBootcampWithCapacitiesDTOToModel(BootcampWithCapacitiesByBootcampIdListResDTO dto) {
        Bootcamp bootcamp = Bootcamp.builder().id(dto.getId()).build();
        List<CapacityTechnologies> capacities = dto.getCapacidades().stream()
                .map(this::convertCapacityWithTechDTOToModel)
                .toList();

        return BootcampCapacities.builder()
                .bootcamp(bootcamp)
                .capacities(capacities)
                .build();
    }
    private CapacityTechnologies convertCapacityWithTechDTOToModel(CapacityWithTechnologiesByCapacityIdDTO dto) {
        Capacity capacity = Capacity.builder().id(dto.getId()).name(dto.getNombre()).build();
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
