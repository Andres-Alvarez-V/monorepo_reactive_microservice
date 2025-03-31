package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class BusinessException extends ProcessorException {

    public BusinessException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }

    public BusinessException(String message, TechnicalMessage technicalMessage) {
        super(message, technicalMessage);
    }




}
