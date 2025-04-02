package co.com.bancolombia.model.capacity.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.BootcampWithCapacities;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampCapacityRepository {
    Flux<BootcampCapacity> saveAllBootcampCapacity(List<BootcampCapacity> bootcampCapacity);
    Flux<BootcampWithCapacities> findBootcampCapacityByBootcampIdIn(List<Long> bootcampIds);

    Mono<PageResponse<BootcampWithCapacities>> findAllBootcampsWithCapacitiesOrderedByCapacityCount(Pagination pagination);
}
