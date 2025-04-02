package co.com.bancolombia.api.dto.bootcampCapacity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BootcampCapacityByCountListReqDTO {
    @Min(value = 1, message = "El limit debe ser mínimo 1")
    private Integer limit;

    @Min(value = 0, message = "El offset debe ser mínimo 0")
    private Integer offset;

    @Pattern(regexp = "cantidadcapacidades", message = "El sortBy solo puede ser 'cantidadcapacidades'")
    private String sortBy;

    @Pattern(regexp = "asc|desc", message = "El sortDirection solo puede ser 'asc' o 'desc'")
    private String sortDirection;
}
