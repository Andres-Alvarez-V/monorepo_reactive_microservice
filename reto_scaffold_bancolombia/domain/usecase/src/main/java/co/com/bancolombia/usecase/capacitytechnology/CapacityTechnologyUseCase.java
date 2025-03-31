package co.com.bancolombia.usecase.capacitytechnology;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import co.com.bancolombia.model.technology.CapacityTechnology;
import co.com.bancolombia.model.technology.CapacityWithTechnologies;
import co.com.bancolombia.model.technology.gateways.CapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CapacityTechnologyUseCase {
    private final CapacityTechnologyRepository capacityTechnologyRepository;

    public Flux<CapacityTechnology> createCapacityTechnology(List<CapacityTechnology> capacityTechnologies) {
        return capacityTechnologyRepository.save(capacityTechnologies)
                .onErrorResume(throwable -> {
                            return Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR));
                        }
                );
    }

    public Flux<CapacityWithTechnologies> findCapacityWithTechnologieByCapacityIdIn(List<Long> capacityIds) {
        return capacityTechnologyRepository.findCapacityWithTechnologieByCapacityIdIn(capacityIds);
    }

    public Mono<PageResponse<CapacityWithTechnologies>> findAllCapacitiesWithTechnologiesOrderedByTechnologyCount(Pagination pagination) {
        return capacityTechnologyRepository.findAllCapacitiesWithTechnologiesOrderedByTechnologyCount(pagination).
                onErrorResume(throwable -> {
                    return Mono.error(new Exception(throwable.getMessage()));
                });
    }
}
