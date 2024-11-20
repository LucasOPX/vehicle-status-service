package vehicle.status.service.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.service.VehicleStatusService;

import java.util.UUID;

@Validated
@Controller("/status")
public class VehicleStatusController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleStatusController.class);

    private final VehicleStatusService vehicleStatusService;

    @Inject
    public VehicleStatusController(VehicleStatusService vehicleStatusService) {
        this.vehicleStatusService = vehicleStatusService;
    }

    @Get("/check")
    public void display() {
        System.out.println("jestem w kontolerze");

    }

    @Post("/check")
    public Mono<VehicleStatusResponse> check(@Valid @Body VehicleStatusRequest request, HttpRequest<?> httpRequest) {
        String requestId = UUID.randomUUID().toString();
        httpRequest.setAttribute("requestId", requestId);
        LOG.info("[{}] Request received: {}", requestId, request);
        if (request == null ||
                request.getVin() == null || request.getVin().isBlank() ||
                request.getFeatures() == null || request.getFeatures().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: VIN must not be null or blank, and Features must not be null or empty");
        }

        return vehicleStatusService.checkStatus(request, requestId)
                .doOnSubscribe(subscription -> LOG.info("[{}] Subscription started", requestId))
                .doOnSuccess(response -> LOG.info("[{}] Request processed successfully: {}", requestId, response))
                .doOnError(throwable -> LOG.error("[{}] Request failed: {}", requestId, throwable.getMessage(), throwable));
    }
}
