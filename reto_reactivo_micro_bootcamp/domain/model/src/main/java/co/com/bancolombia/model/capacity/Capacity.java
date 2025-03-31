package co.com.bancolombia.model.capacity;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Capacity {
    private Long id;
    private String name;
}
