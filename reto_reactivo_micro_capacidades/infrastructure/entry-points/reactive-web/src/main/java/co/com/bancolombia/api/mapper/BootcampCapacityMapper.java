package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.model.capacity.BootcampCapacity;

import java.util.List;

public class BootcampCapacityMapper {
    public static List<BootcampCapacity> mapBootcampCapacityRequestDTOToListBootcampCapacity(BootcampCapacitiesCreateRequestDTO bootcampCapacityDTO) {
        return bootcampCapacityDTO.getIdCapacidades().stream()
                .map(idCapacidad -> BootcampCapacity.builder()
                        .idBootcamp(bootcampCapacityDTO.getIdBootcamp())
                        .idCapacity(idCapacidad)
                        .build())
                .toList();
    }

}
