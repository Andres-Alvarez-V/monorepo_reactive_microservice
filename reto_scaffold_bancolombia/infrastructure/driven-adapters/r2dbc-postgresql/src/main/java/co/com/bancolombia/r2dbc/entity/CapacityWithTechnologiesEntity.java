package co.com.bancolombia.r2dbc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "capacity_technology")
@EqualsAndHashCode(callSuper = true)
public class CapacityWithTechnologiesEntity extends CapacityTechnologyEntity {
    @Column(name="technology_name")
    private String technologyName;
}