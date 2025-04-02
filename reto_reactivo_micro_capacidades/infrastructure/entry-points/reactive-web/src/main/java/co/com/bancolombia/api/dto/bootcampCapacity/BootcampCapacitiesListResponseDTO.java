package co.com.bancolombia.api.dto.bootcampCapacity;

import co.com.bancolombia.api.dto.capacity.CapacityTechnologiesListResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BootcampCapacitiesListResponseDTO {
    private Long id;
    private List<CapacityTechnologiesListResponseDTO> capacidades;
}
