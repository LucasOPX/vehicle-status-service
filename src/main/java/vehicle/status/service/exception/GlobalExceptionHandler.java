package vehicle.status.service.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vehicle.status.service.model.ErrorResponse;

@Singleton
@Produces
public class GlobalExceptionHandler implements ExceptionHandler<Throwable, HttpResponse<ErrorResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Throwable exception) {
        String requestId = request.getAttribute("requestId", String.class).orElse("unknown");

        if (exception instanceof IllegalArgumentException) {
            LOG.warn("[{}] Client error: {}", requestId, exception.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(requestId, exception.getMessage());
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        LOG.error("[{}] Unexpected error: {}", requestId, exception.getMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse(requestId, "Internal Server Error");
        return HttpResponse.serverError(errorResponse);
    }
}
