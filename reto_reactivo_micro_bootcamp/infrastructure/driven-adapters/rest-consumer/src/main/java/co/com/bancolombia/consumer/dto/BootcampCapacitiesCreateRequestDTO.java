package co.com.bancolombia.consumer.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BootcampCapacitiesCreateRequestDTO {
    private Long idBootcamp;
    private List<Long> idCapacidades;
}
