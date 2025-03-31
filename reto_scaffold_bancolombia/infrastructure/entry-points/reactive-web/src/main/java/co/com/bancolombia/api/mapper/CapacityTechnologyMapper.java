package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.capacityTechnology.CapacityTechnologyCreateRequestDTO;
import co.com.bancolombia.api.dto.capacityTechnology.CapacityWithTechnologiesListResponseDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.technology.CapacityTechnology;
import co.com.bancolombia.model.technology.CapacityWithTechnologies;

import java.util.List;

public class CapacityTechnologyMapper {
    public static List<CapacityTechnology> mapCapacityTechnologyRequestDTOToListCapacityTechnology(CapacityTechnologyCreateRequestDTO capacityTechnologyDTO) {
        return capacityTechnologyDTO.getIdTecnologias().stream()
                .map(idTecnologia -> CapacityTechnology.builder()
                        .idCapacity(capacityTechnologyDTO.getIdCapacidad())
                        .idTechnology(idTecnologia)
                        .build())
                .toList();
    }

    public static CapacityWithTechnologiesListResponseDTO mapCapacityWithTechnologiesListResponse(CapacityWithTechnologies capacityWithTechnologies) {
        return CapacityWithTechnologiesListResponseDTO.builder()
                .idCapacidad(capacityWithTechnologies.getCapacityId())
                .tecnologias(
                        capacityWithTechnologies.getTechnologies().stream().map(
                                technology -> CapacityWithTechnologiesListResponseDTO.TechnologyDTO.builder()
                                        .id(technology.getId())
                                        .nombre(technology.getName())
                                        .build()
                        ).toList()
                )
                .build();
    }

    public  static PageResponse<CapacityWithTechnologiesListResponseDTO> mapAllCapacitiesWithTehcnologiesOrderByTechnologyCountResponse (PageResponse<CapacityWithTechnologies> page){
        return PageResponse.<CapacityWithTechnologiesListResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(CapacityTechnologyMapper::mapCapacityWithTechnologiesListResponse)
                        .toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .build();
    }
}
