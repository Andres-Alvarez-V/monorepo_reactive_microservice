package co.com.bancolombia.api.dto.technology;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TechnologyDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String nombre;
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 90, message = "La descripción no puede tener más de 90 caracteres")
    @Size(min = 3, message = "La descripción no puede tener menos de 3 caracteres")
    private String descripcion;
}
