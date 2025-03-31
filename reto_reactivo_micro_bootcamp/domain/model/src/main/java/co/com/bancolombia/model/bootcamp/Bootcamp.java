package co.com.bancolombia.model.bootcamp;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Bootcamp {
    private Long id;
    private String name;
    private String description;
}
