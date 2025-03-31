package co.com.bancolombia.api.dto.capacity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CapacityCreateRequestDTO {

    @NotBlank
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;
    @NotBlank
    @Size(max = 90, message = "La descripción no puede tener más de 90 caracteres")
    private String descripcion;

    @NotNull(message = "La lista de tecnologías no puede ser nula")
    @Size(min = 3, max = 20, message = "La lista debe tener entre 3 y 20 elementos")
    private List<Long> idTecnologias;
}
