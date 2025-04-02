package co.com.bancolombia.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityWithTechnologiesByCapacityIdDTO {
    private Long id;
    private String nombre;
    private List<TechnologyDTO> tecnologias;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechnologyDTO {
        private String nombre;
        private Long id;
    }
}
