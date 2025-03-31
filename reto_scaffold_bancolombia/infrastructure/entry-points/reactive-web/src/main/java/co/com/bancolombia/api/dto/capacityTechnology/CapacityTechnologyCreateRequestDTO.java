package co.com.bancolombia.api.dto.capacityTechnology;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CapacityTechnologyCreateRequestDTO {
    @NotNull
    private Long idCapacidad;

    @NotNull(message = "La lista de tecnologías no puede ser nula")
    @Size(min = 3, max = 20, message = "La lista debe tener entre 3 y 20 elementos")
    private List<Long> idTecnologias;
}
