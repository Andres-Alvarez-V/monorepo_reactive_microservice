package co.com.bancolombia.usecase.bootcamp;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.BootcampCapacities;
import co.com.bancolombia.model.bootcamp.gateways.BootcampRepository;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.capacity.gateways.CapacityConsumer;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import co.com.bancolombia.model.technology.Technology;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapacityConsumer capacityConsumer;


    public Mono<Bootcamp> createBootcampWithCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        return createBootcamp(bootcamp)
                .flatMap(savedBootcamp -> createBootcampCapacities(savedBootcamp, capacities)
                        .thenReturn(savedBootcamp)
                )
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP_WITH_CAPACITIES)));
    }

    private Mono<Bootcamp> createBootcamp(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcamp)
                .onErrorResume(throwable -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP)));
    }

    private Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        return capacityConsumer.createBootcampCapacities(bootcamp, capacities)
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP_WITH_CAPACITIES)));
    }


    public Mono<PageResponse<BootcampCapacities>> listBootcampWithCapacityOrderByName(Pagination pagination) {
        return bootcampRepository.findAllPaginated(pagination)
                .flatMap(bootcampPageResponse -> {
                    List<Bootcamp> bootcamps = bootcampPageResponse.getContent();
                    List<Long> bootcampIds = bootcamps.stream()
                            .map(Bootcamp::getId)
                            .toList();

                    Mono<List<BootcampCapacities>> bootcampCapacitiesMono = capacityConsumer
                            .getCapacitiesByBootcampIds(bootcampIds)
                            .collectList();

                    return bootcampCapacitiesMono.map(
                            bootcampCapacitiesList -> {
                                Map<Long, List<CapacityTechnologies>> capacitiesByBootcampId = bootcampCapacitiesList.stream()
                                        .collect(Collectors.toMap(
                                                ct -> ct.getBootcamp().getId(),
                                                BootcampCapacities::getCapacities
                                        ));

                                List<BootcampCapacities> results = bootcamps.stream()
                                        .map(bootcamp -> BootcampCapacities.builder()
                                                .bootcamp(bootcamp)
                                                .capacities(capacitiesByBootcampId.get(bootcamp.getId()))
                                                .build())
                                        .toList();

                                return PageResponse.<BootcampCapacities>builder()
                                        .content(results)
                                        .totalElements(bootcampPageResponse.getTotalElements())
                                        .totalPages(bootcampPageResponse.getTotalPages())
                                        .currentPage(bootcampPageResponse.getCurrentPage())
                                        .pageSize(bootcampPageResponse.getPageSize())
                                        .build();
                            }
                    );
                });
    }

    public Mono<PageResponse<BootcampCapacities>> listBootcampWithCapacityOrderByCapacityCount(Pagination pagination) {
        Mono<PageResponse<BootcampCapacities>> pageResponseMono = capacityConsumer.getBootcampCapacitiesOrderedByCapacityCount(pagination);

        return pageResponseMono.flatMap(pageResponse -> {
            List<Long> bootcampId = pageResponse.getContent().stream()
                    .map(BootcampCapacities::getBootcamp)
                    .map(Bootcamp::getId)
                    .toList();
            Mono<Map<Long, Bootcamp>> bootcampMapMono = bootcampRepository.findAllByIds(bootcampId)
                    .collectMap(Bootcamp::getId);

            return bootcampMapMono.map(bootcampMap -> {
                List<BootcampCapacities> updatedContent = pageResponse.getContent().stream()
                        .map(bootcampCapacities -> BootcampCapacities.builder()
                                .bootcamp(bootcampMap.get(bootcampCapacities.getBootcamp().getId()))
                                .capacities(bootcampCapacities.getCapacities())
                                .build())
                        .toList();

                return PageResponse.<BootcampCapacities>builder()
                        .content(updatedContent)
                        .totalElements(pageResponse.getTotalElements())
                        .totalPages(pageResponse.getTotalPages())
                        .currentPage(pageResponse.getCurrentPage())
                        .pageSize(pageResponse.getPageSize())
                        .build();
            });
        }).onErrorResume(e -> Mono.error(new Exception("Error while fetching capacities with technologies " + e)));
    }


    public Mono<PageResponse<BootcampCapacities>> listBootcampWithCapacity(Pagination pagination) {
        if("name".equalsIgnoreCase(pagination.getSortBy())) {
            return listBootcampWithCapacityOrderByName(pagination);
        } else if("cantidadcapacidades".equalsIgnoreCase(pagination.getSortBy())) {
            return listBootcampWithCapacityOrderByCapacityCount(pagination);
        } else {
            return Mono.error(new IllegalArgumentException("Invalid sortBy parameter: " + pagination.getSortBy()));
        }
    }
}
