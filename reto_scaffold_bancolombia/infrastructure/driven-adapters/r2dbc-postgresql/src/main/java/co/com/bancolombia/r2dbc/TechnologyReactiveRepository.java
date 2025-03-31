package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyReactiveRepository extends ReactiveCrudRepository<TechnologyEntity, Long>, ReactiveQueryByExampleExecutor<TechnologyEntity> {
    Mono<TechnologyEntity> findByName(String name);
    Flux<TechnologyEntity> findAllBy(Pageable pageable);

}
