package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.technology.CapacityTechnology;
import co.com.bancolombia.model.technology.CapacityWithTechnologies;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.CapacityTechnologyRepository;
import co.com.bancolombia.r2dbc.entity.CapacityTechnologyEntity;
import co.com.bancolombia.r2dbc.entity.CapacityWithTechnologiesEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;


@Repository
public class CapacityTechnologyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        CapacityTechnology,
        CapacityTechnologyEntity,
        Long,
        CapacityTechnologyReactiveRepository> implements CapacityTechnologyRepository {

    public CapacityTechnologyReactiveRepositoryAdapter(CapacityTechnologyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, CapacityTechnology.class));

    }

    @Override
    public Flux<CapacityTechnology> save(List<CapacityTechnology> capacityTechnology) {
        return super.saveAllEntities(Flux.fromIterable(capacityTechnology));
    }

    public Flux<CapacityWithTechnologies> findCapacityWithTechnologieByCapacityIdIn(List<Long> capacityIds) {
        return repository.findCapacityTechnologiesWithTechnologiesByCapacityIdIn(capacityIds)
                .groupBy(CapacityWithTechnologiesEntity::getIdCapacity)
                .flatMap(group -> group.collectList()
                        .map(capacityWithTechnologiesEntities -> {
                            CapacityWithTechnologies capacityWithTechnologies = new CapacityWithTechnologies();
                            capacityWithTechnologies.setCapacityId(group.key());
                            capacityWithTechnologies.setTechnologies(
                                    capacityWithTechnologiesEntities.stream().map(
                                            capacityWithTechnologiesEntity -> Technology.builder()
                                                    .id(capacityWithTechnologiesEntity.getIdTechnology())
                                                    .name(capacityWithTechnologiesEntity.getTechnologyName())
                                                    .build()
                                    ).toList()
                            );
                            return capacityWithTechnologies;
                        }));


    }

    public Mono<PageResponse<CapacityWithTechnologies>> findAllCapacitiesWithTechnologiesOrderedByTechnologyCount(Pagination pagination) {
        Mono<Long> total = repository.countAllCapacities().onErrorResume(e -> {
            System.err.println("Error in countAllCapacities: " + e.getMessage());
            return Mono.error(new RuntimeException("Error counting capacities", e));
        });;
        int offset = pagination.getOffset();
        int limit = pagination.getLimit();
        String sortDirection = pagination.getSortDirection();

        Flux<CapacityWithTechnologiesEntity> content;

        if ("asc".equalsIgnoreCase(sortDirection)) {
            content = repository.findAllCapacitiesWithTechnologiesOrderedByTechnologyCountAsc(offset, limit);
        } else {
            content = repository.findAllCapacitiesWithTechnologiesOrderedByTechnologyCountDesc(offset, limit);
        }

        Mono<List<CapacityWithTechnologies>> capacityWithTechnologiesListMono = content
                .groupBy(CapacityWithTechnologiesEntity::getIdCapacity)
                .flatMap(group -> group.collectList().map(entities -> {
                    CapacityWithTechnologies capacityWithTechnologies = new CapacityWithTechnologies();
                    capacityWithTechnologies.setCapacityId(group.key());
                    capacityWithTechnologies.setTechnologies(
                            entities.stream().map(
                                    entity -> Technology.builder()
                                            .id(entity.getIdTechnology())
                                            .name(entity.getTechnologyName())
                                            .build()
                            ).toList()
                    );
                    return capacityWithTechnologies;
                }))
                .sort(pagination.getSortDirection().equalsIgnoreCase("asc") ?
                                Comparator.comparingInt((CapacityWithTechnologies cwt) -> cwt.getTechnologies().size()) :
                                Comparator.comparingInt((CapacityWithTechnologies cwt) -> cwt.getTechnologies().size()).reversed())
                .collectList()
                .onErrorResume(
                        e -> Mono.error(new RuntimeException("Error while fetching capacities with technologies " + e))
                );

        return total.zipWith(capacityWithTechnologiesListMono, (totalElements, capacityWithTechnologiesList) -> {
            int totalPages = (int) Math.ceil((double) totalElements / limit);
            return PageResponse.<CapacityWithTechnologies>builder()
                    .content(capacityWithTechnologiesList)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(offset / limit)
                    .pageSize(limit)
                    .build();
        }).onErrorResume(
                e -> Mono.error(new RuntimeException("Error while fetching capacities with technologies zipwith" + e.getMessage())));
    }

    public Mono<Long> countAllCapacities() {
        return repository.countAllCapacities();
    }



}
