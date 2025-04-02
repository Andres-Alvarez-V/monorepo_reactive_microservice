package co.com.bancolombia.r2dbc;


import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.BootcampWithCapacities;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.capacity.gateways.BootcampCapacityRepository;
import co.com.bancolombia.r2dbc.entity.BootcampCapacityEntity;
import co.com.bancolombia.r2dbc.entity.BootcampWithCapacityEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Repository
public class BootcampCapacityReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        BootcampCapacity,
        BootcampCapacityEntity,
        Long,
        BootcampCapacityReactiveRepository> implements BootcampCapacityRepository {

    public BootcampCapacityReactiveRepositoryAdapter(BootcampCapacityReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, BootcampCapacity.class));
    }

    public Flux<BootcampCapacity> saveAllBootcampCapacity(List<BootcampCapacity> bootcampCapacity) {
        return super.saveAllEntities(Flux.fromIterable(bootcampCapacity));
    }

    public Flux<BootcampWithCapacities> findBootcampCapacityByBootcampIdIn(List<Long> bootcampIds) {
        return repository.findBootcampCapacityByBootcampIdIn(bootcampIds)
                .groupBy(BootcampWithCapacityEntity::getIdBootcamp)
                .flatMap(group -> group.collectList()
                        .map(bootcampWithCapacityEntities -> {
                            return BootcampWithCapacities.builder()
                                    .id(group.key())
                                    .capacities(
                                            bootcampWithCapacityEntities.stream().map(
                                                    bootcampWithCapacityEntity -> CapacityTechnologies.builder()
                                                            .capacity(
                                                                    Capacity.builder()
                                                                            .id(bootcampWithCapacityEntity.getIdCapacity())
                                                                            .name(bootcampWithCapacityEntity.getCapacityName())
                                                                            .build()
                                                            )
                                                            .technologies(List.of())
                                                            .build()
                                            ).toList()
                                    )
                                    .build();
                        })
                );
    }

    public Mono<PageResponse<BootcampWithCapacities>> findAllBootcampsWithCapacitiesOrderedByCapacityCount(Pagination pagination) {
        Mono<Long> total = repository.count().onErrorResume(e -> {
            System.err.println("Error counting bootcamps: " + e.getMessage());
            return Mono.error(new RuntimeException("Error counting bootcamps", e));
        });
        int offset = pagination.getOffset();
        int limit = pagination.getLimit();
        String sortDirection = pagination.getSortDirection();

        Flux<BootcampWithCapacityEntity> content;

        if ("asc".equalsIgnoreCase(sortDirection)) {
            content = repository.findAllBootcampsWithCapacityOrderedByCapacityCountAsc(offset, limit);
        } else {
            content = repository.findAllBootcampsWithCapacityOrderedByCapacityCountDesc(offset, limit);
        }

        Mono<List<BootcampWithCapacities>> bootcampWithCapacitiesListMono = content
                .groupBy(BootcampWithCapacityEntity::getIdBootcamp)
                .flatMap(group -> group.collectList().map(entities -> {
                    BootcampWithCapacities bootcampWithCapacities = new BootcampWithCapacities();
                    bootcampWithCapacities.setId(group.key());
                    bootcampWithCapacities.setCapacities(
                            entities.stream().map(entity ->
                                    CapacityTechnologies.builder()
                                            .capacity(Capacity.builder()
                                                    .id(entity.getIdCapacity())
                                                    .name(entity.getCapacityName())
                                                    .build())
                                            .build()
                            ).toList()
                    );
                    return bootcampWithCapacities;
                }))
                .sort(pagination.getSortDirection().equalsIgnoreCase("asc") ?
                        Comparator.comparingInt((BootcampWithCapacities cwt) -> cwt.getCapacities().size()) :
                        Comparator.comparingInt((BootcampWithCapacities cwt) -> cwt.getCapacities().size()).reversed())
                .collectList()
                .onErrorResume(
                        e -> Mono.error(new RuntimeException("Error while fetching bootcamps with capacities " + e))
                );


        return total.zipWith(bootcampWithCapacitiesListMono, (totalElements, bootcampWithCapacitiesList) -> {
            int totalPages = (int) Math.ceil((double) totalElements / limit);
            return PageResponse.<BootcampWithCapacities>builder()
                    .content(bootcampWithCapacitiesList)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(offset / limit)
                    .pageSize(limit)
                    .build();
        }).onErrorResume(
                e -> Mono.error(new RuntimeException("Error while fetching bootcamps with capacities zipwith" + e.getMessage())));
    }

    public Mono<Long> countAllCapacities() {
        return repository.countAllCapacities();
    }
}
