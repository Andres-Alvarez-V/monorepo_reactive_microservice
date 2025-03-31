package co.com.bancolombia.model.bootcamp.gateways;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save (Bootcamp bootcamp);
}
