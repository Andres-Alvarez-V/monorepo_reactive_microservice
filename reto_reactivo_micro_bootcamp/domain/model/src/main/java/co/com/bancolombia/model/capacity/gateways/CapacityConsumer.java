package co.com.bancolombia.model.capacity.gateways;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.capacity.Capacity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityConsumer {
    Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities);

}
