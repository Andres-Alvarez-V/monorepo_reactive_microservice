package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.BootcampCapacityEntity;
import co.com.bancolombia.r2dbc.entity.BootcampWithCapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampCapacityReactiveRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapacityEntity> {

    @Query("SELECT bc.id_capacity, bc.id_bootcamp, c.name AS capacity_name " +
            "FROM bootcamp_capacity bc " +
            "INNER JOIN capacity c ON bc.id_capacity = c.id " +
            "WHERE bc.id_bootcamp IN (:bootcampIds)")
    Flux<BootcampWithCapacityEntity> findBootcampCapacityByBootcampIdIn(List<Long> bootcampIds);

    @Query(value = """
            WITH bootcamps_ranked AS (
                SELECT
                    bc.id_bootcamp,
                    COUNT(bc.id_capacity) AS capacity_count
                FROM bootcamp_capacity bc
                GROUP BY bc.id_bootcamp
                ORDER BY capacity_count ASC
                OFFSET :offset LIMIT :limit
            )
            SELECT
                br.id_bootcamp,
                c.id AS id_capacity,
                c.name AS capacity_name
            FROM bootcamps_ranked br
            JOIN bootcamp_capacity bc ON bc.id_bootcamp = br.id_bootcamp
            JOIN capacity c ON c.id = bc.id_capacity
            ORDER BY br.capacity_count ASC
            """)
    Flux<BootcampWithCapacityEntity> findAllBootcampsWithCapacityOrderedByCapacityCountAsc(
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = """
            WITH bootcamps_ranked AS (
                SELECT
                    bc.id_bootcamp,
                    COUNT(bc.id_capacity) AS capacity_count
                FROM bootcamp_capacity bc
                GROUP BY bc.id_bootcamp
                ORDER BY capacity_count DESC
                OFFSET :offset LIMIT :limit
            )
            SELECT
                br.id_bootcamp,
                c.id AS id_capacity,
                c.name AS capacity_name
            FROM bootcamps_ranked br
            JOIN bootcamp_capacity bc ON bc.id_bootcamp = br.id_bootcamp
            JOIN capacity c ON c.id = bc.id_capacity
            ORDER BY br.capacity_count DESC
            """)
    Flux<BootcampWithCapacityEntity> findAllBootcampsWithCapacityOrderedByCapacityCountDesc(
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query("SELECT COUNT(DISTINCT id_bootcamp) FROM bootcamp_capacity")
    Mono<Long> countAllCapacities();
}
