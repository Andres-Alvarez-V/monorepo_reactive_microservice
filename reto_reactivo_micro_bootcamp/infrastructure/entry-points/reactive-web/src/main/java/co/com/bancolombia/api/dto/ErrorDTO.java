package co.com.bancolombia.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private Integer code;
    private String message;
    private String param;
}