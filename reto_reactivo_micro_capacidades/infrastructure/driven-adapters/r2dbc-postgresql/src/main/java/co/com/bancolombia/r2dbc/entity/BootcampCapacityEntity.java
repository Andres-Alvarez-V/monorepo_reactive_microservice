package co.com.bancolombia.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bootcamp_capacity")
public class BootcampCapacityEntity {
    @Id
    private Long id;
    @Column("id_bootcamp")
    private Long idBootcamp;
    @Column("id_capacity")
    private Long idCapacity;
}
