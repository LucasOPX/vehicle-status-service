package vehicle.status.service.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vehicle.status.service.model.ErrorResponse;

@Singleton
@Produces
public class ServiceExceptionHandler implements ExceptionHandler<ServiceException, HttpResponse<ErrorResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ServiceException exception) {
        String requestId = request.getAttribute("requestId", String.class).orElse("unknown");
        LOG.error("[{}] Handling ServiceException: {}", requestId, exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(requestId, exception.getMessage());
        return HttpResponse.badRequest(errorResponse);
    }
}
