package co.com.bancolombia.model.capacity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BootcampCapacity {
    Long id;
    Long idCapacity;
    Long idBootcamp;
}
