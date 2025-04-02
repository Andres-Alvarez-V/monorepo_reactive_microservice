package co.com.bancolombia.model.capacity.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.BootcampCapacities;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityConsumer {
    Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities);

    Flux<BootcampCapacities> getCapacitiesByBootcampIds(List<Long> bootcampIds);

    Mono<PageResponse<BootcampCapacities>> getBootcampCapacitiesOrderedByCapacityCount(Pagination pagination);

}
