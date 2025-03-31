package co.com.bancolombia.model.technology.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.technology.CapacityTechnology;
import co.com.bancolombia.model.technology.CapacityWithTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityTechnologyRepository {
    Flux<CapacityTechnology> save(List<CapacityTechnology> capacityTechnology);
    Flux<CapacityWithTechnologies> findCapacityWithTechnologieByCapacityIdIn(List<Long> capacityIds);
    Mono<PageResponse<CapacityWithTechnologies>> findAllCapacitiesWithTechnologiesOrderedByTechnologyCount(Pagination pagination);
}
