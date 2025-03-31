package co.com.bancolombia.api.dto.technology;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TechnologyResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}