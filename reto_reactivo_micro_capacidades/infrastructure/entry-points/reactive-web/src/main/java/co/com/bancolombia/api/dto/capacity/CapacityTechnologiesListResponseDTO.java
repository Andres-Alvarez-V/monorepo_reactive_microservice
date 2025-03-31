package co.com.bancolombia.api.dto.capacity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CapacityTechnologiesListResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private List<TechnologyDTO> tecnologias;

    @Data
    @Builder
    public static class TechnologyDTO {
        private String nombre;
        private Long id;
    }
}
