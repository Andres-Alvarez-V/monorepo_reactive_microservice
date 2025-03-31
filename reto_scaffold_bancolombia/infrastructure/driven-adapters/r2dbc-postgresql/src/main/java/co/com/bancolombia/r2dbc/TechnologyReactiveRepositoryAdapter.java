package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import co.com.bancolombia.r2dbc.entity.TechnologyEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public class TechnologyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Technology,
        TechnologyEntity,
        Long,
        TechnologyReactiveRepository
> implements TechnologyRepository {
    public TechnologyReactiveRepositoryAdapter(TechnologyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Technology.class));
    }

    @Override
    public Mono<Technology> save(Technology technology) {
        return super.save(technology);
    }

    public Mono<Technology> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
    public Mono<PageResponse<Technology>> findAllPaginated(Pagination pagination) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(pagination.getSortDirection())
                .orElse(Sort.Direction.ASC);

        String sortBy = Optional.ofNullable(pagination.getSortBy()).orElse("name");

        PageRequest pageRequest = PageRequest.of(
                pagination.getOffset() / pagination.getLimit(),
                pagination.getLimit(),
                Sort.by(direction, sortBy)
        );

        Mono<Long> total = repository.count();
        Flux<Technology> content = repository.findAllBy(pageRequest).map(this::toEntity);

        return total.zipWith(content.collectList(), (totalElements, list) -> {
            int totalPages = (int) Math.ceil((double) totalElements / pagination.getLimit());
            return PageResponse.<Technology>builder()
                    .content(list)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(pagination.getOffset() / pagination.getLimit())
                    .pageSize(pagination.getLimit())
                    .build();
        });
    }


}
