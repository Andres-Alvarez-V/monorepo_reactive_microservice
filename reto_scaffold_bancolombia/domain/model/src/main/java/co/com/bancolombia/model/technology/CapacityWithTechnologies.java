package co.com.bancolombia.model.technology;


import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CapacityWithTechnologies {
    private Long capacityId;
    private List<Technology> technologies;
}
