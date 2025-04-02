package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateways.BootcampRepository;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public class BootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
        BootcampEntity,
        Long,
        BootcampReactiveRepository
> implements BootcampRepository {
    public BootcampReactiveRepositoryAdapter(BootcampReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
    }

    public Mono<PageResponse<Bootcamp>> findAllPaginated(Pagination pagination) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(pagination.getSortDirection()).orElseGet(() -> Sort.Direction.ASC);
        String sortBy = Optional.ofNullable(pagination.getSortBy()).orElseGet(() -> "id");

        PageRequest pageRequest = PageRequest.of(
                pagination.getOffset() / pagination.getLimit(),
                pagination.getLimit(),
                Sort.by(direction, sortBy)
        );

        Mono<Long> total = repository.count();
        Flux<Bootcamp> content = repository.findAllBy(pageRequest).map(this::toEntity);

        return total.zipWith(content.collectList(), (totalElements, list) -> {
            int totalPages = (int) Math.ceil((double) totalElements / pagination.getLimit());
            return PageResponse.<Bootcamp>builder()
                    .content(list)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(pagination.getOffset() / pagination.getLimit())
                    .pageSize(pagination.getLimit())
                    .build();
        });
    }

    public Flux<Bootcamp> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids).map(this::toEntity);
    }
}
