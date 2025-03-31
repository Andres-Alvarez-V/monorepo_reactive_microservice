package co.com.bancolombia.model.technology.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.technology.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyConsumer {
    Mono<Void> createCapacityTechnologies(Capacity capacity, List<Technology> technologies);
    Flux<CapacityTechnologies> getTechnologiesByCapacityIds(List<Long> capacityIds);
    Mono<PageResponse<CapacityTechnologies>> getCapacitiesWithTechnologiesOrderedByTechnologyCount(Pagination pagination);
}
