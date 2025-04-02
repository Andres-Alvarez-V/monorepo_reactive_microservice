package co.com.bancolombia.api.dto.capacityTechnology;
import jakarta.validation.constraints.Min;
        import jakarta.validation.constraints.Pattern;
        import lombok.Data;

@Data
public class CapacityWithTechByCountTechListReqDTO {
    @Min(value = 1, message = "El limit debe ser mínimo 1")
    private Integer limit;

    @Min(value = 0, message = "El offset debe ser mínimo 0")
    private Integer offset;

    @Pattern(regexp = "cantidadtecnologias", message = "El sortBy solo puede ser 'cantidadtecnologias'")
    private String sortBy;

    @Pattern(regexp = "asc|desc", message = "El sortDirection solo puede ser 'asc' o 'desc'")
    private String sortDirection;
}