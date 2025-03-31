package co.com.bancolombia.model.common.exceptions;


import co.com.bancolombia.model.common.enums.TechnicalMessage;

public class TechnicalException extends ProcessorException {

    public TechnicalException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause, technicalMessage);
    }

    public TechnicalException(TechnicalMessage technicalMessage) {
        super(technicalMessage.getMessage(), technicalMessage);
    }

}