package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.Pagination;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateways.CapacityRepository;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.r2dbc.entity.CapacityEntity;
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
public class CapacityReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Capacity,
        CapacityEntity,
        Long,
        CapacityReactiveRepository
> implements CapacityRepository {
    public CapacityReactiveRepositoryAdapter(CapacityReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Capacity.class));
    }


    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return super.save(capacity);
    }

    public Mono<PageResponse<Capacity>> findAllPaginated(Pagination pagination) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(pagination.getSortDirection())
                .orElse(Sort.Direction.ASC);
        String sortBy = Optional.ofNullable(pagination.getSortBy()).orElse("id");

        PageRequest pageRequest = PageRequest.of(
                pagination.getOffset()/ pagination.getLimit(),
                pagination.getLimit(),
                Sort.by(direction, sortBy)
        );

        Mono<Long> total = repository.count();
        Flux<Capacity> content = repository.findAllBy(pageRequest).map(this::toEntity);

        return total.zipWith(content.collectList(), (totalElements, list) -> {
            int totalPages = (int) Math.ceil((double) totalElements / pagination.getLimit());
            return PageResponse.<Capacity>builder()
                    .content(list)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .currentPage(pagination.getOffset() / pagination.getLimit())
                    .pageSize(pagination.getLimit())
                    .build();
        });
    }

    public Flux<Capacity> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids).map(this::toEntity);
    }

}
