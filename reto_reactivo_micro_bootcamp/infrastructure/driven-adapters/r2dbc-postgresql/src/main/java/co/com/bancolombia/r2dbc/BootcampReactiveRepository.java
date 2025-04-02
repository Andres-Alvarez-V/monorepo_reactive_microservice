package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BootcampReactiveRepository extends ReactiveCrudRepository<BootcampEntity, Long>, ReactiveQueryByExampleExecutor<BootcampEntity> {

    Flux<BootcampEntity> findAllBy(Pageable pageable);
}
