package co.com.bancolombia.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "bootcamp_capacity")
@EqualsAndHashCode(callSuper = true)
public class BootcampWithCapacityEntity extends BootcampCapacityEntity {
    @Column("capacity_name")
    private String capacityName;

}
