package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.technology.TechnologyDTO;
import co.com.bancolombia.api.dto.technology.TechnologyResponseDTO;
import co.com.bancolombia.model.PageResponse;
import co.com.bancolombia.model.technology.Technology;

public class TechnologyMapper {

    public static Technology mapTechnologyDTOToTechnology(TechnologyDTO technologyDTO) {
        return Technology.builder()
                .name(technologyDTO.getNombre())
                .description(technologyDTO.getDescripcion())
                .build();
    }

    public static TechnologyResponseDTO mapTechnologyToTechnologyResponseDTO(Technology technology) {
        return TechnologyResponseDTO.builder()
                .id(technology.getId())
                .nombre(technology.getName())
                .descripcion(technology.getDescription())
                .build();
    }

    public static PageResponse<TechnologyResponseDTO> mapPageResponse(PageResponse<Technology> page) {
        return PageResponse.<TechnologyResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(TechnologyMapper::mapTechnologyToTechnologyResponseDTO)
                        .toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .build();
    }
}