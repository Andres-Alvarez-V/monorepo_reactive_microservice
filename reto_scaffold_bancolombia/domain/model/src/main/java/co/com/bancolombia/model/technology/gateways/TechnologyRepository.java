package co.com.bancolombia.model.technology.gateways;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.technology.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TechnologyRepository {
    Mono<Technology> save(Technology technology);
    Mono<Technology> findByName(String name);
    Mono<PageResponse<Technology>> findAllPaginated(Pagination pagination);
}
