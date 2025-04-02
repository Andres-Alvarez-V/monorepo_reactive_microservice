package co.com.bancolombia.model.capacity;

import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BootcampWithCapacities {
    private Long id;
    private List<CapacityTechnologies> capacities;
}
