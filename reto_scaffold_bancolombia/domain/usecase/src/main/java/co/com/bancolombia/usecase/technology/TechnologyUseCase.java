package co.com.bancolombia.usecase.technology;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.common.exceptions.TechnicalException;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class TechnologyUseCase {
    private final TechnologyRepository technologyRepository;
    public Mono<Technology> createTechnology(Technology technology) {
        return technologyRepository.findByName(technology.getName())
                .flatMap(existingTechnology -> Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)))
                .then(technologyRepository.save(technology))
                .onErrorResume(throwable -> {
                    return Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR));
                });
    }

    public Mono<PageResponse<Technology>> listTechnologies(Pagination pagination) {
        return technologyRepository.findAllPaginated(pagination);
    }
}
