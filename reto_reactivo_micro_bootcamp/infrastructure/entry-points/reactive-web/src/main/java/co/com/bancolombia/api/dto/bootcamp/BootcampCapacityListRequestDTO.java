package co.com.bancolombia.api.dto.bootcamp;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BootcampCapacityListRequestDTO {
    @Min(value = 1, message = "El limit debe ser mínimo 1")
    private Integer limit;

    @Min(value = 0, message = "El offset debe ser mínimo 0")
    private Integer offset;

    @Pattern(regexp = "nombre|cantidadcapacidades", message = "El sortBy solo puede ser 'nombre' o 'cantidadcapacidades'")
    private String sortBy;

    @Pattern(regexp = "asc|desc", message = "El sortDirection solo puede ser 'asc' o 'desc'")
    private String sortDirection;
}
