package co.com.bancolombia.model.bootcamp;


import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BootcampCapacities {
    private Bootcamp bootcamp;
    private List<CapacityTechnologies> capacities;
}
