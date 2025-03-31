// CapacityTechnologyReactiveRepository.java
package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.CapacityTechnologyEntity;
import co.com.bancolombia.r2dbc.entity.CapacityWithTechnologiesEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityTechnologyReactiveRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long>, ReactiveQueryByExampleExecutor<CapacityTechnologyEntity> {
    @Query("SELECT ct.id_capacity, ct.id_technology, t.name AS technology_name " +
            "FROM capacity_technology ct " +
            "INNER JOIN technology t ON ct.id_technology = t.id " +
            "WHERE ct.id_capacity IN (:capacityIds)")
    Flux<CapacityWithTechnologiesEntity> findCapacityTechnologiesWithTechnologiesByCapacityIdIn(List<Long> capacityIds);

    @Query(value = """
            WITH capacities_ranked AS (
                SELECT
                    ct.id_capacity,
                    COUNT(ct.id_technology) AS technology_count
                FROM capacity_technology ct
                GROUP BY ct.id_capacity
                ORDER BY technology_count ASC
                OFFSET :offset LIMIT :limit
            )
            SELECT
                cr.id_capacity,
                t.id AS id_technology,
                t.name AS technology_name
            FROM capacities_ranked cr
            JOIN capacity_technology ct ON ct.id_capacity = cr.id_capacity
            JOIN technology t ON t.id = ct.id_technology
            ORDER BY cr.technology_count ASC
            """)
    Flux<CapacityWithTechnologiesEntity> findAllCapacitiesWithTechnologiesOrderedByTechnologyCountAsc(
                                                                                                    @Param("offset") int offset,
                                                                                                   @Param("limit") int limit);

    @Query(value = """
            WITH capacities_ranked AS (
                SELECT
                    ct.id_capacity,
                    COUNT(ct.id_technology) AS technology_count
                FROM capacity_technology ct
                GROUP BY ct.id_capacity
                ORDER BY technology_count DESC
                OFFSET :offset LIMIT :limit
            )
            SELECT
                cr.id_capacity,
                t.id AS id_technology,
                t.name AS technology_name
            FROM capacities_ranked cr
            JOIN capacity_technology ct ON ct.id_capacity = cr.id_capacity
            JOIN technology t ON t.id = ct.id_technology
            ORDER BY cr.technology_count DESC
            """)
    Flux<CapacityWithTechnologiesEntity> findAllCapacitiesWithTechnologiesOrderedByTechnologyCountDesc(
            @Param("offset") int offset,
            @Param("limit") int limit);


    @Query("SELECT COUNT(DISTINCT id_capacity) FROM capacity_technology")
    Mono<Long> countAllCapacities();

}