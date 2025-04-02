package co.com.bancolombia.usecase.bootcampCapacity;


import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.BootcampWithCapacities;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.capacity.gateways.BootcampCapacityRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyConsumer;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BootcampCapacityUseCase {
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final TechnologyConsumer technologyConsumer;

    public Flux<BootcampCapacity> createBootcampCapacity(List<BootcampCapacity> bootcampCapacities) {
        return bootcampCapacityRepository.saveAllBootcampCapacity(bootcampCapacities)
                .onErrorResume(throwable -> {
                            return Mono.error(new Exception("Error createBootcampCapacity usecase" + throwable));
                        }
                );
    }

    public Mono<List<BootcampWithCapacities>> findBootcampWithCapacitiesByCapacityIdIn(List<Long> bootcampIds) {
        Mono<List<BootcampWithCapacities>> bootcampWithCapacitiesMono = bootcampCapacityRepository.findBootcampCapacityByBootcampIdIn(bootcampIds).collectList();

        Mono<Map<Long, List<Technology>>> technologiesByCapacityIdMono = bootcampWithCapacitiesMono
                .flatMap(this::getAllUniqueCapacityIds)
                .flatMap(capacityIdsList -> technologyConsumer.getTechnologiesByCapacityIds(capacityIdsList).collectList())
                .map(capacityTechnologiesList -> capacityTechnologiesList.stream()
                    .collect(Collectors.toMap(
                            ct -> ct.getCapacity().getId(),
                            CapacityTechnologies::getTechnologies
                    )));

        return Mono.zip(bootcampWithCapacitiesMono, technologiesByCapacityIdMono, this::combineBootcampsAndTechnologies);
    }

    public Mono<PageResponse<BootcampWithCapacities>> findAllBootcampWithCapacitiesOrderedByCapacityCount(Pagination pagination) {
        Mono<PageResponse<BootcampWithCapacities>> bootcampWithCapacitiesMono = bootcampCapacityRepository.findAllBootcampsWithCapacitiesOrderedByCapacityCount(pagination)
                .onErrorResume(throwable -> {
                    return Mono.error(new Exception(throwable.getMessage()));
                });

        Mono<Map<Long, List<Technology>>> technologiesByCapacityIdMono = bootcampWithCapacitiesMono
                .map(PageResponse::getContent)
                .flatMap(this::getAllUniqueCapacityIds)
                .flatMap(capacityIdsList -> technologyConsumer.getTechnologiesByCapacityIds(capacityIdsList).collectList())
                .map(capacityTechnologiesList -> capacityTechnologiesList.stream()
                        .collect(Collectors.toMap(
                                ct -> ct.getCapacity().getId(),
                                CapacityTechnologies::getTechnologies
                        )));

        return Mono.zip(bootcampWithCapacitiesMono, technologiesByCapacityIdMono, (pageResponse, technologiesMap) -> {
            List<BootcampWithCapacities> updatedContent = this.combineBootcampsAndTechnologies(pageResponse.getContent(), technologiesMap);

            return PageResponse.<BootcampWithCapacities>builder()
                    .content(updatedContent)
                    .totalElements(pageResponse.getTotalElements())
                    .totalPages(pageResponse.getTotalPages())
                    .currentPage(pageResponse.getCurrentPage())
                    .pageSize(pageResponse.getPageSize())
                    .build();
        });
    }

    private Mono<List<Long>> getAllUniqueCapacityIds(List<BootcampWithCapacities> bootcampWithCapacitiesList) {
        return Mono.just(bootcampWithCapacitiesList.stream()
                .flatMap(bwc -> bwc.getCapacities().stream()
                        .map(CapacityTechnologies::getCapacity)
                        .map(Capacity::getId))
                .distinct().toList());
    }

    private List<BootcampWithCapacities> combineBootcampsAndTechnologies(
            List<BootcampWithCapacities> bootcampWithCapacitiesList,
            Map<Long, List<Technology>> technologiesByCapacityId) {
        return bootcampWithCapacitiesList.stream()
                .map(bwc -> {
                    List<CapacityTechnologies> updatedCapacities = bwc.getCapacities().stream()
                            .map(capacityTechnologies -> {
                                Capacity capacity = capacityTechnologies.getCapacity();
                                return CapacityTechnologies.builder()
                                        .capacity(capacity)
                                        .technologies(technologiesByCapacityId.get(capacity.getId()))
                                        .build();
                            })
                            .toList();

                    return BootcampWithCapacities.builder()
                            .id(bwc.getId())
                            .capacities(updatedCapacities)
                            .build();
                })
                .toList();
    }

}
