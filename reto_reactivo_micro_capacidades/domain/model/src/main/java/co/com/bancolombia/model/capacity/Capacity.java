package co.com.bancolombia.model.capacity;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Capacity {
    private Long id;
    private String name;
    private String description;
}
