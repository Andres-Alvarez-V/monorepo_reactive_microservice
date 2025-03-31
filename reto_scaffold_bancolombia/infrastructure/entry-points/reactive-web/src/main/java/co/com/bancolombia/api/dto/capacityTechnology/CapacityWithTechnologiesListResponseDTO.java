package co.com.bancolombia.api.dto.capacityTechnology;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CapacityWithTechnologiesListResponseDTO {
    private Long idCapacidad;
    private List<TechnologyDTO> tecnologias;

    @Data
    @Builder
    public static class TechnologyDTO {
        private String nombre;
        private Long id;
    }

}
