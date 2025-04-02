package co.com.bancolombia.model.bootcamp.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.capacity.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampRepository {
    Mono<PageResponse<Bootcamp>> findAllPaginated(Pagination pagination);
    Mono<Bootcamp> save (Bootcamp bootcamp);

    Flux<Bootcamp> findAllByIds(List<Long> ids);
}
