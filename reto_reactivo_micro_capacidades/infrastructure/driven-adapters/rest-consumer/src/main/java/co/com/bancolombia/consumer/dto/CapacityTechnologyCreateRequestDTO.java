package co.com.bancolombia.consumer.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityTechnologyCreateRequestDTO {
    private Long idCapacidad;
    private List<Long> idTecnologias;
}
