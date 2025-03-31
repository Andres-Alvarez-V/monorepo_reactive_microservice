package co.com.bancolombia.model.capacity.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityRepository {
    Mono<Capacity> save(Capacity capacity);
    Mono<PageResponse<Capacity>> findAllPaginated(Pagination pagination);

    Flux<Capacity> findAllByIds(List<Long> ids);
}
