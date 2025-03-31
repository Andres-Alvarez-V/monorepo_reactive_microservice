package co.com.bancolombia.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CapacityWithTechnologiesByCapacityIdListResDTO {
    private Long idCapacidad;
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
