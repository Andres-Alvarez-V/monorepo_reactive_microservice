package co.com.bancolombia.usecase.capacity;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.capacity.gateways.CapacityRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyConsumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CapacityUseCase {

    private final CapacityRepository capacityRepository;
    private final TechnologyConsumer technologyConsumer;

    public Mono<PageResponse<CapacityTechnologies>> listCapacityWithTechnologiesOrderByName(Pagination pagination) {
        return capacityRepository.findAllPaginated(pagination)
                .flatMap(capacityPageResponse -> {
                    List<Capacity> capacities = capacityPageResponse.getContent();
                    List<Long> capacityIds = capacities.stream()
                            .map(Capacity::getId)
                            .toList();

                    Mono<List<CapacityTechnologies>> capacityTechnologiesMono = technologyConsumer
                            .getTechnologiesByCapacityIds(capacityIds)
                            .collectList();

                    return capacityTechnologiesMono.map(
                            capacityTechnologiesList -> {
                                Map<Long, List<Technology>> technologiesByCapacityId = capacityTechnologiesList.stream()
                                        .collect(Collectors.toMap(
                                                ct -> ct.getCapacity().getId(),
                                                CapacityTechnologies::getTechnologies
                                        ));
                                List<CapacityTechnologies> results = capacities.stream()
                                        .map(capacity -> CapacityTechnologies.builder()
                                                .capacity(capacity)
                                                .technologies(technologiesByCapacityId.get(capacity.getId()))
                                                .build())
                                        .toList();

                                return PageResponse.<CapacityTechnologies>builder()
                                        .content(results)
                                        .totalElements(capacityPageResponse.getTotalElements())
                                        .totalPages(capacityPageResponse.getTotalPages())
                                        .currentPage(capacityPageResponse.getCurrentPage())
                                        .pageSize(capacityPageResponse.getPageSize())
                                        .build();
                            }
                    );
                });
    }

    public Mono<PageResponse<CapacityTechnologies>> listCapacityWithTechnologiesOrderByTechnologyCount(Pagination pagination) {
        Mono<PageResponse<CapacityTechnologies>> pageResponseMono  = technologyConsumer.getCapacitiesWithTechnologiesOrderedByTechnologyCount(pagination);

        return  pageResponseMono.flatMap(pageResponse -> {
            List<Long> capacitiesId = pageResponse.getContent().stream()
                    .map(CapacityTechnologies::getCapacity)
                    .map(Capacity::getId)
                    .collect(Collectors.toList());

            Mono<Map<Long, Capacity>> capacityMapMono = capacityRepository.findAllByIds(capacitiesId)
                    .collectMap(Capacity::getId);

            return capacityMapMono.map(capacityMap -> {
                        List<CapacityTechnologies> updatedContent = pageResponse.getContent().stream()
                                .map(capacityTechnologies -> {
                                    Capacity capacity = capacityMap.get(capacityTechnologies.getCapacity().getId());
                                    return CapacityTechnologies.builder()
                                            .capacity(capacity)
                                            .technologies(capacityTechnologies.getTechnologies())
                                            .build();
                                })
                                .toList();

                        return PageResponse.<CapacityTechnologies>builder()
                                .content(updatedContent)
                                .totalElements(pageResponse.getTotalElements())
                                .totalPages(pageResponse.getTotalPages())
                                .currentPage(pageResponse.getCurrentPage())
                                .pageSize(pageResponse.getPageSize())
                                .build();
                    }
            );
        }).onErrorResume(
                e -> Mono.error(new Exception("Error while fetching capacities with technologies " + e))
        );
    }

    public Mono<PageResponse<CapacityTechnologies>> listCapacityWithTechnologies(Pagination pagination) {
        if ("name".equalsIgnoreCase(pagination.getSortBy())) {
            return listCapacityWithTechnologiesOrderByName(pagination);
        } else if ("cantidadtecnologias".equalsIgnoreCase(pagination.getSortBy())) {
            return listCapacityWithTechnologiesOrderByTechnologyCount(pagination);
        } else {
             return Mono.error(new IllegalArgumentException("Invalid sortBy parameter: " + pagination.getSortBy()));
        }
    }

    public Mono<Capacity>  createCapacityWithTechnologies(Capacity capacity, List<Technology> technologies) {
        return createCapacity(capacity)
                .flatMap(savedCapacity -> createCapacityTechnologies(savedCapacity, technologies)
                        .thenReturn(savedCapacity)
                )
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_CAPACITY_WITH_TECHNOLOGIES)));
    }

    private Mono<Capacity> createCapacity(Capacity capacity) {
        return capacityRepository.save(capacity)
                .onErrorResume(e -> {
                    return Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_CAPACITY));
                });
    }

    private Mono<Void> createCapacityTechnologies(Capacity capacityWithId, List<Technology> technologies) {
        return technologyConsumer.createCapacityTechnologies(capacityWithId, technologies)
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_CAPACITY_WITH_TECHNOLOGIES)));
    }

}
