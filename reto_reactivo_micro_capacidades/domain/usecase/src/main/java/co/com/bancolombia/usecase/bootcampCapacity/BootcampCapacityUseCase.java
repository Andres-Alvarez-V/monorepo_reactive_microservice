package co.com.bancolombia.usecase.bootcampCapacity;


import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.gateways.BootcampCapacityRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class BootcampCapacityUseCase {
    private final BootcampCapacityRepository bootcampCapacityRepository;

    public Flux<BootcampCapacity> createBootcampCapacity(List<BootcampCapacity> bootcampCapacities) {
        return bootcampCapacityRepository.saveAllBootcampCapacity(bootcampCapacities)
                .onErrorResume(throwable -> {
                            return Mono.error(new Exception("Error createBootcampCapacity usecase" + throwable));
                        }
                );
    }

}
