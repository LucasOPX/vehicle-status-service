package vehicle.status.service.exception;

public class ThirdPartyServiceException extends ServiceException {
    public ThirdPartyServiceException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}