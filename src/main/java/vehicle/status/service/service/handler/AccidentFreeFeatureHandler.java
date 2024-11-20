package vehicle.status.service.service.handler;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import vehicle.status.service.client.InsuranceClient;
import vehicle.status.service.exception.ServiceException;
import vehicle.status.service.model.VehicleStatusRequest;
import vehicle.status.service.model.VehicleStatusResponse;
import vehicle.status.service.model.enums.Feature;

import java.time.Duration;

@Singleton
public class AccidentFreeFeatureHandler implements FeatureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AccidentFreeFeatureHandler.class);

    private final InsuranceClient insuranceClient;

    @Inject
    public AccidentFreeFeatureHandler(InsuranceClient insuranceClient) {
        this.insuranceClient = insuranceClient;
    }

    @Override
    public boolean supports(Feature feature) {
        return Feature.ACCIDENT_FREE.equals(feature);
    }

    @Override
    public Mono<Void> handle(VehicleStatusRequest request, VehicleStatusResponse.VehicleStatusResponseBuilder responseBuilder, String requestId) {
        return insuranceClient.getAccidentReport(request.getVin(), requestId)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(this::isRetriableException)
                        .doBeforeRetry(retrySignal -> LOG.warn("[{}] Retry attempt #{}", requestId, retrySignal.totalRetries())))
                .map(insuranceReport -> {
                    int claims = insuranceReport.getReport().getClaims();
                    responseBuilder.accidentFree(claims == 0);
                    return insuranceReport;
                })
                .doOnError(error -> LOG.error("[{}] Error in AccidentFreeFeatureHandler: {}", requestId, error.getMessage()))
                .onErrorResume(error -> Mono.error(new ServiceException("Failed to process accident_free feature")))
                .then();
    }

    private boolean isRetriableException(Throwable throwable) {
        // Define retriable exceptions (e.g., network errors)
        return true; // Simplified for this example
    }
}
