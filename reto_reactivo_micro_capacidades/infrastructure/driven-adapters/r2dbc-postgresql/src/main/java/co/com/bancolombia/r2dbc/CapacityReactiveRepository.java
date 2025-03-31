package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CapacityEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CapacityReactiveRepository extends ReactiveCrudRepository<CapacityEntity, Long>, ReactiveQueryByExampleExecutor<CapacityEntity> {

    Flux<CapacityEntity> findAllBy(Pageable pageable);
}
