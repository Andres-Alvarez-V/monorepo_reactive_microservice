package co.com.bancolombia.api.dto.technology;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TechnologyListRequestDTO {
    @Min(value = 1, message = "El limit debe ser mínimo 1")
    private Integer limit;

    @Min(value = 0, message = "El offset debe ser mínimo 0")
    private Integer offset;

    @Pattern(regexp = "nombre", message = "El sortBy solo puede ser 'nombre'")
    private String sortBy;

    @Pattern(regexp = "asc|desc", message = "El sortDirection solo puede ser 'asc' o 'desc'")
    private String sortDirection;
}