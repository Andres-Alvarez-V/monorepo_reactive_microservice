package co.com.bancolombia.r2dbc.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capacity_technology")
public class CapacityTechnologyEntity {
    @Id
    private Long id;
    @Column(name = "id_technology")
    private Long idTechnology;
    @Column(name = "id_capacity")
    private Long idCapacity;
}
