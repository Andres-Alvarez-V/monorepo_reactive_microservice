package co.com.bancolombia.api.dto.bootcampCapacity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class BootcampCapacitiesCreateRequestDTO {
    @NotNull
    private Long idBootcamp;

    @NotNull(message = "La lista de capacidades no puede ser nula")
    @Size(min = 1, max = 4, message = "La lista debe tener entre 1 y 4 elementos")
    private List<Long> idCapacidades;
}
