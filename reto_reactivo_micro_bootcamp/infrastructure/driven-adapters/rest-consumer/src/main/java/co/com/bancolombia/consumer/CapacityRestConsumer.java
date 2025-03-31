package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.dto.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.gateways.CapacityConsumer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacityRestConsumer implements CapacityConsumer {
    private final WebClient client;


    @CircuitBreaker(name = "createCapacityTechnologies")
    public Mono<Void> createBootcampCapacities(Bootcamp bootcamp, List<Capacity> capacities) {
        BootcampCapacitiesCreateRequestDTO request = BootcampCapacitiesCreateRequestDTO.builder()
                .idBootcamp(bootcamp.getId())
                .idCapacidades(capacities.stream().map(Capacity::getId).toList())
                .build();
        return client.post()
                .uri("/api/bootcamp-capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), BootcampCapacitiesCreateRequestDTO.class)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
