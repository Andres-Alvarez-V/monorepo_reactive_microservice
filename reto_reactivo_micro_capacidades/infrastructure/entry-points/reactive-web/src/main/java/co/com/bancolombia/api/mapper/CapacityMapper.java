package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.capacity.CapacityCreateRequestDTO;
import co.com.bancolombia.api.dto.capacity.CapacityTechnologiesListResponseDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.capacity.Capacity;
import co.com.bancolombia.model.capacity.CapacityTechnologies;
import co.com.bancolombia.model.technology.Technology;

import java.util.List;
import java.util.stream.Collectors;

public class CapacityMapper {

    public static Capacity mapCapacityCreateRequestDTOToCapacity(CapacityCreateRequestDTO requestDTO) {
        return Capacity.builder()
                .name(requestDTO.getNombre())
                .description(requestDTO.getDescripcion())
                .build();
    }

    public static List<Technology> mapIdTecnologiasToTechnologies(List<Long> idTecnologias) {
        return idTecnologias.stream()
                .map(id -> Technology.builder().id(id).build())
                .collect(Collectors.toList());
    }

    public static CapacityTechnologiesListResponseDTO mapCapacityTechnologiesToDto(CapacityTechnologies capacityTechnologies) {
        return CapacityTechnologiesListResponseDTO.builder()
                .id(capacityTechnologies.getCapacity().getId())
                .nombre(capacityTechnologies.getCapacity().getName())
                .descripcion(capacityTechnologies.getCapacity().getDescription())
                .tecnologias(
                        capacityTechnologies.getTechnologies() == null ? List.of() :
                                capacityTechnologies.getTechnologies().stream().map(
                                        technology -> CapacityTechnologiesListResponseDTO.TechnologyDTO.builder()
                                                .id(technology.getId())
                                                .nombre(technology.getName())
                                                .build()
                                ).toList()

                )
                .build();

    }

    public static PageResponse<CapacityTechnologiesListResponseDTO> mapPageResponse(PageResponse<CapacityTechnologies> page) {
        return PageResponse.<CapacityTechnologiesListResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(CapacityMapper::mapCapacityTechnologiesToDto)
                        .toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .build();
    }
}