package co.com.bancolombia.model.capacity.gateways;

import co.com.bancolombia.model.capacity.BootcampCapacity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BootcampCapacityRepository {
    Flux<BootcampCapacity> saveAllBootcampCapacity(List<BootcampCapacity> bootcampCapacity);
}
