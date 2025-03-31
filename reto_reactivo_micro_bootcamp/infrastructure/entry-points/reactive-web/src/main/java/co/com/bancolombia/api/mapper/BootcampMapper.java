package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.bootcamp.BootcampCreateRequestDTO;
import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.capacity.Capacity;

import java.util.List;

public class BootcampMapper {

    public static Bootcamp mapBootcampCreateRequestDTOToBootcamp(BootcampCreateRequestDTO bootcampCreateRequestDTO) {
        return Bootcamp.builder()
                .name(bootcampCreateRequestDTO.getNombre())
                .description(bootcampCreateRequestDTO.getDescripcion())
                .build();
    }

    public static List<Capacity> mapIdCapacidadesToCapacities(List<Long> idCapacidades) {
        return idCapacidades.stream()
                .map(id -> Capacity.builder().id(id).build())
                .toList();
    }

}
