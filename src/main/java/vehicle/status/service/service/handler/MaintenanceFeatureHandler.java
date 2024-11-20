package vehicle.status.service.service.handler;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import vehicle.status.service.client.MaintenanceClient;
import vehicle.status.service.exception.ServiceException;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;

import java.time.Duration;

@Singleton
public class MaintenanceFeatureHandler implements FeatureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MaintenanceFeatureHandler.class);

    private final MaintenanceClient maintenanceClient;

    @Inject
    public MaintenanceFeatureHandler(MaintenanceClient maintenanceClient) {
        this.maintenanceClient = maintenanceClient;
    }

    @Override
    public boolean supports(Feature feature) {
        return Feature.MAINTENANCE.equals(feature);
    }

    @Override
    public Mono<Void> handle(VehicleStatusRequest request, VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder, String requestId) {
        return maintenanceClient.getMaintenanceInfo(request.getVin(), requestId)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetriableException)
                        .doBeforeRetry(retrySignal -> LOG.warn("[{}] Retry attempt #{}", requestId, retrySignal.totalRetries())))
                .map(maintenanceInfo -> {
                    String maintenanceScore = mapMaintenanceScore(maintenanceInfo.getMaintenanceFrequency());
                    responseBuilder.maintenanceScore(maintenanceScore);
                    return maintenanceInfo;
                })
                .doOnError(error -> LOG.error("[{}] Error in MaintenanceFeatureHandler: {}", requestId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new ServiceException("Failed to process maintenance feature")))
                .then();
    }

    private boolean isRetriableException(Throwable throwable) {
        // Define retriable exceptions
        return true; // Simplified for this example
    }

    private String mapMaintenanceScore(String frequency) {
        switch (frequency) {
            case "very-low":
            case "low":
                return "poor";
            case "medium":
                return "average";
            case "high":
                return "good";
            default:
                throw new ServiceException("Unknown maintenance frequency: " + frequency);
        }
    }
}
