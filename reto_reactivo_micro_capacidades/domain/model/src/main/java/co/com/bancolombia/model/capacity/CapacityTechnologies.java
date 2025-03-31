package co.com.bancolombia.model.capacity;


import co.com.bancolombia.model.technology.Technology;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityTechnologies {
    private Capacity capacity;
    private List<Technology> technologies;
}
