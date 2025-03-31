package co.com.bancolombia.model.technology;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CapacityTechnology {
    Long id;
    Long idTechnology;
    Long idCapacity;
}
