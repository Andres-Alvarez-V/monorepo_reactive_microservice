package co.com.bancolombia.model.technology;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Technology {
    private Long id;
    private String name;
    private String description;
}
