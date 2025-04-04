package co.com.bancolombia.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BootcampWithCapacitiesByBootcampIdListResDTO {
    private Long id;
    private List<CapacityWithTechnologiesByCapacityIdDTO> capacidades;
}
