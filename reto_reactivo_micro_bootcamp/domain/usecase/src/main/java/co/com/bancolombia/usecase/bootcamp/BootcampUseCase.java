package co.com.bancolombia.usecase.bootcamp;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateways.BootcampRepository;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateways.CapacityConsumer;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class BootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapacityConsumer capacityConsumer;


    public Mono<Bootcamp> createBootcampWithCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        return createBootcamp(bootcamp)
                .flatMap(savedBootcamp -> createBootcampCapacities(savedBootcamp, capacities)
                        .thenReturn(savedBootcamp)
                )
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP_WITH_CAPACITIES)));
    }

    private Mono<Bootcamp> createBootcamp(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcamp)
                .onErrorResume(throwable -> {
                    return Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP));
                });
    }

    private Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        return capacityConsumer.createBootcampCapacities(bootcamp, capacities)
                .onErrorResume(e -> Mono.error(new TechnicalException(TechnicalMessage.ERROR_CREATING_BOOTCAMP_WITH_CAPACITIES)));
    }

}
