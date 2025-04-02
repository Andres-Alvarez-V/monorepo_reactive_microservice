package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacitiesCreateRequestDTO;
import co.com.bancolombia.api.dto.bootcampCapacity.BootcampCapacitiesListResponseDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.BootcampWithCapacities;

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

    public static List<BootcampCapacitiesListResponseDTO> mapBootcampWithCapacitiesListResponse(List<BootcampWithCapacities> bootcampWithCapacities){
        return bootcampWithCapacities.stream().map( bwc ->
                BootcampCapacitiesListResponseDTO.builder()
                        .id(bwc.getId())
                        .capacidades(
                                bwc.getCapacities().stream().map(CapacityMapper::mapCapacityTechnologiesToDto).toList()
                        )
                        .build()
        ).toList();
    }

    public static PageResponse<BootcampCapacitiesListResponseDTO> mapAllBootcampWithCapacityOrderByCapacityCountResponse(PageResponse<BootcampWithCapacities> page) {
        return PageResponse.<BootcampCapacitiesListResponseDTO>builder()
                .content(
                        BootcampCapacityMapper.mapBootcampWithCapacitiesListResponse(page.getContent())
                )
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .build();

    }

}
